package com.gome.idea.plugins.jira.util;

/**
 * @author xiehai1
 * @date 2017/05/10 16:35
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class Base64Util {
    private Base64Util(){

    }

    public static String base64(String source){
        return new sun.misc.BASE64Encoder().encode(source.getBytes());
    }

    public static String jiraBase64(String username, String password){
        StringBuilder sb = new StringBuilder(username);
        sb.append(":");
        sb.append(password);
        return "Basic " + base64(sb.toString());
    }
}
