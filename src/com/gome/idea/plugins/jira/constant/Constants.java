package com.gome.idea.plugins.jira.constant;

/**
 * @author xiehai1
 * @date 2017/05/10 15:49
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public interface Constants {
    interface JIRA {
        /**
         * jira用户名密码验证
         */
        String VERFIY = "/rest/auth/1/session";
    }

    interface Http {
        /**
         * http连接超时时间
         */
        int CONNECT_TIMEOUT = 5 * 1000;
    }
}
