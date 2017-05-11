package com.gome.idea.plugins.jira.util;

import com.gome.idea.plugins.jira.GJiraSettings;
import com.gome.idea.plugins.jira.constant.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.net.URLEncoder;
import java.text.MessageFormat;

/**
 * @author xiehai1
 * @date 2017/05/11 15:51
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class JiraHttpUtil {
    private static final GJiraSettings settings = GJiraSettings.me();
    public static boolean login(){
        return login(settings.getJiraUrl(), settings.getUsername(), settings.getPassword());
    }

    /**
     * jira 登录校验
     * @param username
     * @param password
     * @return
     */
    public static boolean login(String url, String username, String password){
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpPost post = new HttpPost(new URI(url + Constants.JIRA.VERFIY));
            setJiraHeader(post, username, password);
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

    /**
     * jira http请求授权header
     * @param base
     */
    public static void setJiraHeader(HttpRequestBase base){
        setJiraHeader(base, settings.getUsername(), settings.getPassword());
    }

    public static void setJiraHeader(HttpRequestBase base, String username, String password){
        base.setHeader("Authorization", Base64Util.jiraBase64(username, password));
    }

    /**
     * 当日是否更新工作日志
     * @return
     */
    public static boolean isTodayLoged(){
        try{
            CloseableHttpClient client = HttpClients.createDefault();
            String jql = MessageFormat.format("assignee={0} and worklogDate=now()", settings.getUsername());
            String param = URLEncoder.encode(jql, "UTF-8");
            HttpGet get = new HttpGet(settings.getJiraUrl() + Constants.JIRA.SEARCH + "?jql=" + param);
            setJiraHeader(get);
            CloseableHttpResponse response = client.execute(get);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                String json = EntityUtils.toString(response.getEntity());
                JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
                return jsonObject.get("total").getAsInt() > 0;
            }

            return false;
        }catch (Exception e){
            return false;
        }
    }
}
