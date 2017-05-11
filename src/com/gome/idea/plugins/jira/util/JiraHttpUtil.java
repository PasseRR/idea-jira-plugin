package com.gome.idea.plugins.jira.util;

import com.gome.idea.plugins.jira.GJiraSettings;
import com.gome.idea.plugins.jira.constant.Constants;
import com.google.gson.JsonObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URI;

/**
 * @author xiehai1
 * @date 2017/05/11 15:51
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class JiraHttpUtil {
    private static final GJiraSettings settings = GJiraSettings.me();
    public static boolean login(){
        return login(settings.getUsername(), settings.getPassword());
    }

    public static boolean login(String username, String password){
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(new URI(settings.getJiraUrl() + Constants.JIRA.VERFIY));
            setJiraHeader(post);
            JsonObject json = new JsonObject();
            json.addProperty("username", username);
            json.addProperty("password", password);
            StringEntity entity = new StringEntity(json.toString());
            entity.setContentType(Constants.Http.CONTENT_TYPE_JSON);
            post.setEntity(entity);
            CloseableHttpResponse response = client.execute(post);
            return HttpStatus.SC_OK == response.getStatusLine().getStatusCode();
        } catch (Exception e1) {
            return false;
        }
    }

    public static void setJiraHeader(HttpRequestBase base){
        base.setHeader("Authorization", Base64Util.jiraBase64(settings.getUsername(), settings.getPassword()));
    }
}
