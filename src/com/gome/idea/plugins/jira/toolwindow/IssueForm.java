package com.gome.idea.plugins.jira.toolwindow;

import com.gome.idea.plugins.jira.GJiraSettings;
import com.gome.idea.plugins.jira.GJiraUi;
import com.gome.idea.plugins.jira.constant.Constants;
import com.gome.idea.plugins.jira.util.Base64Util;
import com.gome.idea.plugins.jira.vo.IssueVo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * tool window issues form
 *
 * @author xiehai1
 * @date 2017/05/10 19:23
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class IssueForm implements GJiraUi {
    private static IssueForm instance;
    private JPanel rootPanel;
    private JTree issueTree;
    private JPopupMenu popupMenu;

    public IssueForm() {
        this.popupMenu = new JPopupMenu();
        final JMenuItem log = new JMenuItem("记录工作日志", new ImageIcon(this.getClass().getResource("/icon/log.png")));
        log.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 记录工作日志
            }
        });
        final JMenuItem time = new JMenuItem("预估时间", new ImageIcon(this.getClass().getResource("/icon/time.png")));
        time.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 预估时间
            }
        });

        final JMenuItem refresh = new JMenuItem("刷新", new ImageIcon(this.getClass().getResource("/icon/refresh.png")));
        refresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                IssueForm.this.reload();
            }
        });
        this.popupMenu.add(log);
        this.popupMenu.add(time);
        this.popupMenu.addSeparator();
        this.popupMenu.add(refresh);

        issueTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreePath path = IssueForm.this.issueTree.getPathForLocation(e.getX(), e.getY());
                if (e.getButton() == MouseEvent.BUTTON3) { // 右键
                    log.setEnabled(false);
                    time.setEnabled(false);
                    if (path != null) {
                        IssueForm.this.issueTree.setSelectionPath(path);
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) IssueForm.this.issueTree.getLastSelectedPathComponent();
                        if(!node.isRoot()){ // 非根节点
                            log.setEnabled(true);
                            IssueVo issueVo = (IssueVo) node.getUserObject();
                            if(issueVo.getTimeOriginalEstimate() == null){
                                time.setEnabled(true);
                            }
                        }
                    }
                    IssueForm.this.popupMenu.show(IssueForm.this.issueTree, e.getX(), e.getY());
                }
            }
        });
    }

    public static IssueForm me() {
        if (null == instance) {
            synchronized (IssueForm.class) {
                if (null == instance) {
                    instance = new IssueForm();
                }
            }
        }

        // 重新加载数据
        instance.reload();

        return instance;
    }

    @Override
    public JComponent getRootComponent() {
        return this.rootPanel;
    }

    @Override
    public boolean isModify() {
        return false;
    }

    @Override
    public void reset() {

    }

    /**
     * 重新加载数据
     */
    protected void reload() {
        List<IssueVo> issues = this.getIssues();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("任务列表");
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        for (IssueVo issueVo : issues) {
            treeModel.insertNodeInto(
                    new DefaultMutableTreeNode(issueVo),
                    root,
                    root.getChildCount()
            );
        }
        this.issueTree.setModel(treeModel);
        this.issueTree.setCellRenderer(new GJiraDefaultTreeCellRenderer());
        this.issueTree.updateUI();
    }

    /**
     * 获得当前所有任务
     *
     * @return issues
     */
    private List<IssueVo> getIssues() {
        List<IssueVo> issues = new ArrayList<IssueVo>();
        final GJiraSettings settings = GJiraSettings.me();
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            String jql = MessageFormat.format("status != {0} and assignee = {1}", "Done", settings.getUsername());
            String param = URLEncoder.encode(jql, "UTF-8");
            HttpGet get = new HttpGet(settings.getJiraUrl() + Constants.JIRA.SEARCH + "?jql=" + param);
            get.setHeader("Authorization", Base64Util.jiraBase64(settings.getUsername(), settings.getPassword()));
            CloseableHttpResponse response = client.execute(get);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                String json = EntityUtils.toString(response.getEntity());
                JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                JsonArray issueArray = jsonObject.getAsJsonArray("issues");
                if (issueArray.size() > 0) {
                    for (int i = 0, len = issueArray.size(); i < len; i++) {
                        JsonObject issue = issueArray.get(i).getAsJsonObject();
                        IssueVo issueVo = new IssueVo();
                        issueVo.setKey(issue.get("key").getAsString());
                        JsonObject fields = issue.get("fields").getAsJsonObject();
                        issueVo.setSummary(fields.get("summary").getAsString());
                        JsonElement timeOriginalEstimate = fields.get("timeoriginalestimate");
                        issueVo.setTimeOriginalEstimate(timeOriginalEstimate.isJsonNull() ? null : timeOriginalEstimate.getAsLong());
                        JsonObject status = fields.get("status").getAsJsonObject();
                        issueVo.setStatus(status.get("name").getAsString());
                        JsonObject issueType = fields.get("issuetype").getAsJsonObject();
                        issueVo.setIssueType(issueType.get("name").getAsString());
                        issues.add(issueVo);
                    }
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return issues;
    }
}
