package com.gome.idea.plugins.jira.toolwindow;

import com.gome.idea.plugins.jira.AbstractGJiraUi;
import com.gome.idea.plugins.jira.constant.Constants;
import com.gome.idea.plugins.jira.vo.IssueVo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class IssueForm extends AbstractGJiraUi {
    private static IssueForm instance;
    private JPanel rootPanel;
    private JTree issueTree;
    private JPopupMenu popupMenu;
    private Project project;

    public IssueForm() {
        this.popupMenu = new JPopupMenu();
        final JMenuItem log = new JMenuItem("记录工作日志", new ImageIcon(this.getClass().getResource("/icon/log.png")));
        log.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 记录工作日志
            }
        });
        final JMenuItem time = new JMenuItem("预估时间", new ImageIcon(this.getClass().getResource("/icon/time.png")));
        time.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 预估时间
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) IssueForm.this.issueTree.getLastSelectedPathComponent();
                if (null != node) {
                    IssueVo issueVo = (IssueVo) node.getUserObject();
                    boolean flg = IssueForm.this.updateOriginalEstimate(issueVo);
                    Notification n = flg ? new Notification("GJira", "jira预估时间", "预估成功!", NotificationType.INFORMATION)
                            : new Notification("GJira", "jira预估时间", "预估失败!", NotificationType.WARNING);
                    Notifications.Bus.notify(n, project);
                    IssueForm.this.reload();
                }
            }
        });

        final JMenuItem refresh = new JMenuItem("刷新", new ImageIcon(this.getClass().getResource("/icon/refresh.png")));
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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
                        if (!node.isRoot()) { // 非根节点
                            log.setEnabled(true);
                            IssueVo issueVo = (IssueVo) node.getUserObject();
                            if (issueVo.getTimeOriginalEstimate() == null) {
                                time.setEnabled(true);
                            }
                        }
                    }
                    IssueForm.this.popupMenu.show(IssueForm.this.issueTree, e.getX(), e.getY());
                }
            }
        });
    }

    public IssueForm(Project project) {
        this();
        this.project = project;
    }

    public static IssueForm me(Project project) {
        if (null == instance) {
            synchronized (IssueForm.class) {
                if (null == instance) {
                    instance = new IssueForm(project);
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
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            String jql = MessageFormat.format("status != {0} and assignee = {1}", "Done", super.getUsername());
            String param = URLEncoder.encode(jql, "UTF-8");
            HttpGet get = new HttpGet(super.getJiraUrl() + Constants.JIRA.SEARCH + "?jql=" + param);
            super.header(get);
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

    /**
     * 更新预估时间
     *
     * @param issueVo
     */
    private boolean updateOriginalEstimate(IssueVo issueVo) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPut put = new HttpPut(super.getJiraUrl() + Constants.JIRA.ESTIMATE + issueVo.getKey());
            super.header(put);
            JsonArray timeTrackingArray = new JsonArray();
            JsonObject edit = new JsonObject();
            JsonObject originalEstimate = new JsonObject();
            // 默认预估3d
            originalEstimate.addProperty("originalEstimate", "3d");
            edit.add("edit", originalEstimate);
            timeTrackingArray.add(edit);
            JsonObject timetracking = new JsonObject();
            timetracking.add("timetracking", timeTrackingArray);
            JsonObject update = new JsonObject();
            update.add("update", timetracking);
            StringEntity json = new StringEntity(update.toString());
            json.setContentType("application/json");
            put.setEntity(json);
            CloseableHttpResponse response = client.execute(put);
            return HttpStatus.SC_NO_CONTENT == response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
