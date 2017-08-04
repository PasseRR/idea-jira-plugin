package com.gome.idea.plugins.jira.toolwindow;

import com.gome.idea.plugins.jira.util.JiraHttpUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowManagerAdapter;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * jira tool window
 * @author xiehai1
 * @date 2017/05/10 17:14
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class GJiraToolWindow implements ToolWindowFactory {
    private ToolWindow root;
    private Project project;
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.root = toolWindow;
        this.project = project;
        this.reload();

        // toolWindow 状态变化监听
        ToolWindowManagerEx.getInstanceEx(project).addToolWindowManagerListener(
            new ToolWindowManagerAdapter() {
                @Override
                public void stateChanged() {
                    // plugin.xml ToolWindow id
                    ToolWindow jira = ToolWindowManagerEx.getInstance(GJiraToolWindow.this.project).getToolWindow("GJira");
                    // 激活ToolWindow做刷新操作
                    if (jira != null && jira.isVisible()) {
                        GJiraToolWindow.this.reload();
                    }
                }
            }
        );
    }

    /**
     * tool window reload
     */
    public void reload() {
        boolean flg = JiraHttpUtil.login();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        JComponent component;
        if (!flg) {
            // 提示用户配置登录信息不合法
            // 跳转到settings配置
            component = new IllegalForm(this).getRootComponent();
        } else {
            component = new IssueForm(this).getRootComponent();
        }
        ContentManager contentManager = this.root.getContentManager();
        final String contentName = "GJira-Control";
        Content current = contentManager.findContent(contentName);
        if (null != current) {
            contentManager.removeContent(current, true);
        }
        Content content = contentFactory.createContent(component, contentName, false);
        contentManager.addContent(content);
    }

    public Project getProject(){
        return this.project;
    }
}
