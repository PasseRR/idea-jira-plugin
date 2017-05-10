package com.gome.idea.plugins.jira.toolwindow;

import com.gome.idea.plugins.jira.GJiraUi;

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
    private static IllegalForm instance;
    public IllegalForm() {
        toSettingsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 跳转settings配置
                // TODO
            }
        });
    }

    public static IllegalForm me(){
        if (null == instance) {
            synchronized (IllegalForm.class) {
                if(null == instance){
                    instance = new IllegalForm();
                }
            }
        }

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
}
