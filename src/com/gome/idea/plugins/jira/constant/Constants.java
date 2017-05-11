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
        /**
         * issure查询
         */
        String SEARCH = "/rest/api/2/search";
        /**
         * issue预估时间
         */
        String ESTIMATE = "/rest/api/2/issue/";
    }

    interface Http {
        /**
         * http连接超时时间
         */
        int CONNECT_TIMEOUT = 5 * 1000;
    }
}
