package com.gome.idea.plugins.jira;

import com.gome.idea.plugins.jira.util.JiraHttpUtil;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * jira http抽象ui
 * @author xiehai1
 * @date 2017/05/11 11:36
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public abstract class AbstractGJiraUi implements GJiraUi{
    private static final GJiraSettings SETTINGS = GJiraSettings.me();
    protected void header(HttpRequestBase base){
        JiraHttpUtil.setJiraHeader(base);
    }

    protected String getUsername(){
        return SETTINGS.getUsername();
    }

    protected String getJiraUrl(){
        return SETTINGS.getJiraUrl();
    }

    protected String getPassword(){
        return SETTINGS.getPassword();
    }
}
