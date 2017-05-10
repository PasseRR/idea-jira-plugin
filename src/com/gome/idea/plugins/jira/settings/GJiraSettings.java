package com.gome.idea.plugins.jira.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

/**
 * Gjira配置持久化
 * @author xiehai1
 * @date 2017/05/10 14:51
 * @Copyright(c) gome inc Gome Co.,LTD
 */
@State(
        name = "GJiraSetting",
        storages = {
                @Storage(
                        id = "GJiraSetting",
                        file = "$APP_CONFIG$/format.xml"
                )
        }
)
public class GJiraSettings implements PersistentStateComponent<Element> {
    private String jiraUrl;
    private String username;
    private String password;

    public GJiraSettings() {

    }

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("GJiraSetting");
        element.setAttribute("jiraUrl", this.getJiraUrl());
        element.setAttribute("username", this.getUsername());
        element.setAttribute("password", this.getPassword());
        return element;
    }

    @Override
    public void loadState(Element element) {
        this.setJiraUrl(element.getAttributeValue("jiraUrl"));
        this.setUsername(element.getAttributeValue("username"));
        this.setPassword(element.getAttributeValue("password"));
    }

    public static GJiraSettings me(){
        return ServiceManager.getService(GJiraSettings.class);
    }

    public String getJiraUrl() {
        return jiraUrl;
    }

    public void setJiraUrl(String jiraUrl) {
        this.jiraUrl = jiraUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
