package com.gome.idea.plugins.jira.toolwindow;

import com.gome.idea.plugins.jira.GJiraSettings;
import com.gome.idea.plugins.jira.constant.Constants;
import com.gome.idea.plugins.jira.util.Base64Util;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * jira tool window
 *
 * @author xiehai1
 * @date 2017/05/10 17:14
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class GJiraToolWindow implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        boolean flg = this.isLegal();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        javax.swing.JComponent component;
        if(!flg){
            // 提示用户配置登录信息不合法
            // 跳转到settings配置
            component = IllegalForm.me().getRootComponent();
        }else{
            component = IssueForm.me().getRootComponent();
        }
        Content content = contentFactory.createContent(component, "Control", false);
        toolWindow.getContentManager().addContent(content);
    }

    /**
     * 判断配置用户名密码是否合法
     *
     * @return
     */
    private boolean isLegal() {
        final GJiraSettings settings = GJiraSettings.me();
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet get = new HttpGet(settings.getJiraUrl() + Constants.JIRA.VERFIY);
            get.setHeader("Authorization", Base64Util.jiraBase64(settings.getUsername(), settings.getPassword()));
            CloseableHttpResponse response = client.execute(get);
            return HttpStatus.SC_OK == response.getStatusLine().getStatusCode();
        } catch (IOException e1) {
            return false;
        }
    }
}
