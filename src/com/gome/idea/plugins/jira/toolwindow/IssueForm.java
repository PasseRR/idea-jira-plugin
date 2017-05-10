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
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiehai1
 * @date 2017/05/10 19:23
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class IssueForm implements GJiraUi {
    private static IssueForm instance;
    private JPanel rootPanel;
    private JTree issueTree;

    public IssueForm(){

    }

    public static IssueForm me(){
        if (null == instance) {
            synchronized (IssueForm.class) {
                if(null == instance){
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
    protected void reload(){
        List<IssueVo> issues = this.getIssues();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("任务列表");
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        for(IssueVo issueVo : issues){
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
     * @return issues
     */
    private List<IssueVo> getIssues(){
        List<IssueVo> issues = new ArrayList<IssueVo>();
        final GJiraSettings settings = GJiraSettings.me();
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            String jql = MessageFormat.format("status != {0} and assignee = {1}", "Done", settings.getUsername());
            String param = URLEncoder.encode(jql, "UTF-8");
            HttpGet get = new HttpGet(settings.getJiraUrl() + Constants.JIRA.SEARCH + "?jql=" + param);
            get.setHeader("Authorization", Base64Util.jiraBase64(settings.getUsername(), settings.getPassword()));
            CloseableHttpResponse response = client.execute(get);
            if(HttpStatus.SC_OK == response.getStatusLine().getStatusCode()){
                String json = EntityUtils.toString(response.getEntity());
                JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                JsonArray issueArray = jsonObject.getAsJsonArray("issues");
                if(issueArray.size() > 0){
                    for(int i = 0, len = issueArray.size(); i < len; i ++){
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
