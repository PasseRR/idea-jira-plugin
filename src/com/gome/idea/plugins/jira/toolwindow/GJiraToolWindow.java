package com.gome.idea.plugins.jira.toolwindow;

import com.gome.idea.plugins.jira.util.JiraHttpUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ex.ToolWindowManagerAdapter;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * jira tool window
 *
 * @author xiehai1
 * @date 2017/05/10 17:14
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class GJiraToolWindow implements ToolWindowFactory {
    private ToolWindow root;
    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow toolWindow) {
        this.root = toolWindow;
        this.reload();

        // toolWindow 状态变化监听
        ((ToolWindowManagerEx) ToolWindowManagerEx.getInstance(project)).addToolWindowManagerListener(
                new ToolWindowManagerAdapter() {
                    @Override
                    public void stateChanged() {
                        ToolWindow jira = ToolWindowManagerEx.getInstance(project).getToolWindow("jira");
                        if (jira != null) {
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
        javax.swing.JComponent component;
        if (!flg) {
            // 提示用户配置登录信息不合法
            // 跳转到settings配置
            component = IllegalForm.me().getRootComponent();
        } else {
            component = IssueForm.me(this).getRootComponent();
        }
        Content current = root.getContentManager().findContent("Control");
        if (null != current) {
            root.getContentManager().removeContent(current, true);
        }
        Content content = contentFactory.createContent(component, "Control", false);
        root.getContentManager().addContent(content);
    }
}
