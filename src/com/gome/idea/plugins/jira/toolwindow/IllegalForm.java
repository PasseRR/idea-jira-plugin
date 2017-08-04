package com.gome.idea.plugins.jira.toolwindow;

import com.gome.idea.plugins.jira.GJiraUi;
import com.intellij.openapi.options.ShowSettingsUtil;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 非法配置tool window form
 * @author xiehai1
 * @date 2017/05/10 17:42
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class IllegalForm implements GJiraUi{
    private JPanel rootPanel;
    private JButton toSettingsButton;
    private JLabel illegalLabel;
    private JButton refreshButton;
    private GJiraToolWindow toolWindow;
    private static IllegalForm instance;
    public IllegalForm() {
        toSettingsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 跳转settings配置
                ShowSettingsUtil.getInstance().showSettingsDialog(IllegalForm.this.toolWindow.getProject(), "Gjira");
            }
        });
        refreshButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                IllegalForm.this.toolWindow.reload();
            }
        });
    }

    public IllegalForm(GJiraToolWindow toolWindow){
        this();
        this.toolWindow = toolWindow;
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
}
