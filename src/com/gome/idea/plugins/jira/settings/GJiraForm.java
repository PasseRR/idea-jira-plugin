package com.gome.idea.plugins.jira.settings;

import com.gome.idea.plugins.jira.constant.Constants;
import com.gome.idea.plugins.jira.util.Base64Util;
import com.intellij.openapi.ui.Messages;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

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
                try {
                    CloseableHttpClient client = HttpClients.createDefault();
                    HttpGet get = new HttpGet(GJiraForm.this.getJiraUrl());
                    CloseableHttpResponse response = client.execute(get);
                    if(HttpStatus.SC_OK == response.getStatusLine().getStatusCode()){
                        Messages.showMessageDialog("连接成功!", "GJira", Messages.getInformationIcon());
                    }else{
                        Messages.showMessageDialog("连接失败!", "GJira", Messages.getErrorIcon());
                    }
                } catch (IOException e1) {
                    Messages.showMessageDialog("连接失败!", "GJira", Messages.getErrorIcon());
                }
            }
        });

        // jira登录验证
        jiraLoginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    CloseableHttpClient client = HttpClients.createDefault();
                    HttpGet get = new HttpGet(GJiraForm.this.getJiraUrl() + Constants.JIRA.VERFIY);
                    get.setHeader("Authorization", Base64Util.jiraBase64(GJiraForm.this.getUsername(), GJiraForm.this.getPassword()));
                    CloseableHttpResponse response = client.execute(get);
                    if(HttpStatus.SC_OK == response.getStatusLine().getStatusCode()){
                        Messages.showMessageDialog("验证成功!", "GJira", Messages.getInformationIcon());
                    }else{
                        Messages.showMessageDialog("验证失败!", "GJira", Messages.getErrorIcon());
                    }
                } catch (IOException e1) {
                    Messages.showMessageDialog("验证失败!", "GJira", Messages.getErrorIcon());
                }
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

    @Override
    public boolean isModify() {
        return !this.jiraUrlTextField.getText().equals(GJiraSettings.me().getJiraUrl())
                || !this.usernameTextField.getText().equals(GJiraSettings.me().getUsername())
                || !new String(this.passwordField.getPassword()).equals(GJiraSettings.me().getUsername());
    }

    @Override
    public void reset() {
        this.jiraUrlTextField.setText(GJiraSettings.me().getJiraUrl());
        this.usernameTextField.setText(GJiraSettings.me().getUsername());
        this.passwordField.setText(GJiraSettings.me().getPassword());
    }

    protected String getJiraUrl(){
        return this.jiraUrlTextField.getText();
    }

    protected String getUsername(){
        return this.usernameTextField.getText();
    }

    protected String getPassword(){
        return new String(this.passwordField.getPassword());
    }
}
