package com.gome.idea.plugins.jira.toolwindow;

import com.gome.idea.plugins.jira.AbstractGJiraUi;
import com.gome.idea.plugins.jira.GJiraNotificationTimer;
import com.gome.idea.plugins.jira.constant.Constants;
import com.gome.idea.plugins.jira.util.JiraHttpUtil;
import com.gome.idea.plugins.jira.util.JiraTimeFormatUtil;
import com.gome.idea.plugins.jira.vo.IssueVo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.DateUtils;
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
import java.util.*;

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
    private GJiraToolWindow toolWindow;

    public IssueForm() {
        this.popupMenu = new JPopupMenu();
        final JMenuItem log = new JMenuItem("记录工作日志", new ImageIcon(this.getClass().getResource("/icon/log.png")));
        log.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 记录工作日志
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) IssueForm.this.issueTree.getLastSelectedPathComponent();
                if (null != node) {
                    IssueVo issueVo = (IssueVo) node.getUserObject();
                    boolean flg = IssueForm.this.log(issueVo);
                    final Notification n = flg ? new Notification("GJira", "jira工作日志记录", "记录成功!", NotificationType.INFORMATION)
                        : new Notification("GJira", "jira工作日志记录", "记录失败!", NotificationType.ERROR);
                    Notifications.Bus.notify(n);
                    new GJiraNotificationTimer(n).start();
                    IssueForm.this.reload();
                }
            }
        });
        final JMenuItem time = new JMenuItem("同步时间", new ImageIcon(this.getClass().getResource("/icon/time.png")));
        time.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 预估时间
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) IssueForm.this.issueTree.getLastSelectedPathComponent();
                if (null != node) {
                    IssueVo issueVo = (IssueVo) node.getUserObject();
                    boolean flg = IssueForm.this.updateOriginalEstimate(issueVo);
                    final Notification n = flg ? new Notification("GJira", "jira时间同步", "同步成功!", NotificationType.INFORMATION)
                        : new Notification("GJira", "jira时间同步", "同步失败!", NotificationType.ERROR);
                    Notifications.Bus.notify(n);
                    new GJiraNotificationTimer(n).start();
                    IssueForm.this.reload();
                }
            }
        });

        final JMenuItem status = new JMenuItem("完成", new ImageIcon(this.getClass().getResource("/icon/done.png")));
        status.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 完成Issue
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) IssueForm.this.issueTree.getLastSelectedPathComponent();
                if (null != node) {
                    IssueVo issueVo = (IssueVo) node.getUserObject();
                    boolean flg = IssueForm.this.done(issueVo);
                    final Notification n = flg ? new Notification("GJira", "完成", "完成成功!", NotificationType.INFORMATION)
                        : new Notification("GJira", "完成", "完成失败!", NotificationType.ERROR);
                    Notifications.Bus.notify(n);
                    new GJiraNotificationTimer(n).start();
                    IssueForm.this.reload();
                }
            }
        });

        final JMenuItem refresh = new JMenuItem("刷新", new ImageIcon(this.getClass().getResource("/icon/refresh.png")));
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                IssueForm.this.toolWindow.reload();
            }
        });
        this.popupMenu.add(log);
        this.popupMenu.add(time);
        this.popupMenu.add(status);
        this.popupMenu.addSeparator();
        this.popupMenu.add(refresh);

        issueTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                TreePath path = IssueForm.this.issueTree.getPathForLocation(e.getX(), e.getY());
                if (e.getButton() == MouseEvent.BUTTON3) { // 右键
                    log.setEnabled(false);
                    time.setEnabled(false);
                    status.setEnabled(false);
                    if (path != null) {
                        IssueForm.this.issueTree.setSelectionPath(path);
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) IssueForm.this.issueTree.getLastSelectedPathComponent();
                        if (!node.isRoot()) { // 非根节点
                            log.setEnabled(true);
                            status.setEnabled(true);
                            IssueVo issueVo = (IssueVo) node.getUserObject();
                            // 已有实际工作时间 可以同步预估时间和工作时间
                            if (issueVo.getTimespent() != null
                                && !issueVo.getTimespent().equals(issueVo.getTimeOriginalEstimate())) {
                                time.setEnabled(true);
                            }
                        }
                    }
                    IssueForm.this.popupMenu.show(IssueForm.this.issueTree, e.getX(), e.getY());
                }
            }
        });
    }

    public IssueForm(GJiraToolWindow toolWindow) {
        this();
        this.toolWindow = toolWindow;
    }

    public static IssueForm me(GJiraToolWindow toolWindow) {
        if (null == instance) {
            synchronized (IssueForm.class) {
                if (null == instance) {
                    instance = new IssueForm(toolWindow);
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
        boolean flg = JiraHttpUtil.isTodayLoged();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(flg);
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
            String jql = MessageFormat.format("resolution = {0} and assignee = {1}", "Unresolved", super.getUsername());
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
                        JsonElement timespent = fields.get("timespent");
                        issueVo.setTimespent(timespent.isJsonNull() ? null : timespent.getAsLong());
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
     * 同步预估时间和实际工作时间
     * 保持预估时间和实际工作时间一致
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
            // 设置预估时间和工作时间一致
            if (issueVo.getTimespent() == null) {
                return false;
            }
            originalEstimate.addProperty("originalEstimate", JiraTimeFormatUtil.formatTime(issueVo.getTimespent()).intValue() + "d");
            originalEstimate.addProperty("remainingEstimate", "0m");
            edit.add("edit", originalEstimate);
            timeTrackingArray.add(edit);
            JsonObject timetracking = new JsonObject();
            timetracking.add("timetracking", timeTrackingArray);
            JsonObject update = new JsonObject();
            update.add("update", timetracking);
            StringEntity json = new StringEntity(update.toString());
            json.setContentType(Constants.Http.CONTENT_TYPE_JSON);
            put.setEntity(json);
            CloseableHttpResponse response = client.execute(put);
            return HttpStatus.SC_NO_CONTENT == response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 记录工作日志
     *
     * @return
     */
    private boolean log(IssueVo issueVo) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(MessageFormat.format(super.getJiraUrl() + Constants.JIRA.LOG, issueVo.getKey()));
            super.header(post);
            JsonObject body = new JsonObject();
            // 2017-05-09T09:13:12.091+0000 时间格式WORKFLOW
            StringBuilder sb = new StringBuilder();
            sb.append(DateUtils.formatDate(new Date(), "yyyy-MM-dd")); // 日期
            sb.append("T"); // 占位
            sb.append("09:00:00.000"); // 时间
            sb.append("+0800"); // 时区
            body.addProperty("started", sb.toString());
            // 默认8小时
            body.addProperty("timeSpentSeconds", 8 * 60 * 60);
            StringEntity entity = new StringEntity(body.toString());
            entity.setContentType(Constants.Http.CONTENT_TYPE_JSON);
            post.setEntity(entity);
            CloseableHttpResponse response = client.execute(post);
            return HttpStatus.SC_CREATED == response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            return false;
        }
    }

    // 21 开始开发
    // 31 开发完成
    // 131 开始联调
    // 141 联调成功
    private static final List<String> TRANSITIONS = Arrays.asList("21", "31", "131", "141");

    private boolean done(IssueVo issueVo) {
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            for (String transitionId : TRANSITIONS) {
                HttpPost post = new HttpPost(MessageFormat.format(super.getJiraUrl() + Constants.JIRA.WORKFLOW, issueVo.getKey()));
                super.header(post);
                JsonObject body = new JsonObject();
                JsonObject transition = new JsonObject();
                transition.addProperty("id", transitionId);
                body.add("transition", transition);
                StringEntity entity = new StringEntity(body.toString());
                entity.setContentType(Constants.Http.CONTENT_TYPE_JSON);
                post.setEntity(entity);
                client.execute(post);
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
