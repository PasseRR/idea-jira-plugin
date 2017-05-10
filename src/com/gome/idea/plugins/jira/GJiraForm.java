package com.gome.idea.plugins.jira;

import com.intellij.openapi.ui.Messages;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * settings ui
 * @author xiehai1
 * @date 2017/05/10 11:22
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class GJiraForm implements GJiraUi{
    private JTextField jiraUrlTextField;
    private JButton testJiraConnectionButton;
    private JPanel rootPanel;
    private JLabel usernameLabel;
    private JTextField usernameTextField;
    private JLabel jiralUrlLabel;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton jiraLoginButton;
    private static GJiraForm instance = null;

    private GJiraForm() {
        // jira服务器验证
        testJiraConnectionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Messages.showMessageDialog("连接成功!", "GJira", Messages.getInformationIcon());
            }
        });
        // jira登录验证
        jiraLoginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Messages.showMessageDialog("验证成功!", "GJira", Messages.getInformationIcon());
            }
        });
    }

    public static GJiraForm me() {
        if (null == instance) {
            synchronized (GJiraForm.class) {
                if(null == instance){
                    instance = new GJiraForm();
                }
            }
        }

        return instance;
    }

    @Override
    public JComponent getRootComponent() {
        return this.rootPanel;
    }
}
