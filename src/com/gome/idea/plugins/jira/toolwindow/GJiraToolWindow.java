package com.gome.idea.plugins.jira.toolwindow;

import com.gome.idea.plugins.jira.util.JiraHttpUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ex.ProjectManagerEx;
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
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        reload(toolWindow);

        // toolWindow 状态变化监听
        ToolWindowManagerEx.getInstanceEx(project).addToolWindowManagerListener(
            new ToolWindowManagerAdapter() {
                @Override
                public void stateChanged() {
                    // 对所有打开的idea实例进行reload
                    Project[] projects = ProjectManagerEx.getInstanceEx().getOpenProjects();
                    for (Project p : projects) {
                        // plugin.xml ToolWindow id
                        ToolWindow jira = ToolWindowManagerEx.getInstance(p).getToolWindow("GJira");
                        // 激活ToolWindow做刷新操作
                        if (jira != null && jira.isVisible()) {
                            reload(jira);
                        }
                    }
                }
            }
        );
    }

    static void reload(ToolWindow root){
        JComponent component = getDisplayComponent(root);
        ContentManager contentManager = root.getContentManager();
        ContentFactory contentFactory = contentManager.getFactory();
        final String contentName = "GJira-Control";
        contentManager.removeAllContents(true);
        Content content = contentFactory.createContent(component, contentName, true);
        contentManager.addContent(content);
    }

    /**
     * 获得要展示的JComponent
     * @return {@link JComponent}
     */
    private static JComponent getDisplayComponent(ToolWindow root) {
        boolean flg = JiraHttpUtil.login();
        // 提示用户配置登录信息不合法
        // 跳转到settings配置
        return flg ? new IssueForm(root).getRootComponent()
            : new IllegalForm(root).getRootComponent();
    }
}
