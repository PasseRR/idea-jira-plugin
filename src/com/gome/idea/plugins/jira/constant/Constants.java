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
        /**
         * 记录工作日志
         */
        String LOG = "/rest/api/2/issue/{0}/worklog";
        /**
         * 工作流更新
         */
        String WORKFLOW = "/rest/api/2/issue/{0}/transitions";
    }

    interface Http {
        /**
         * http连接超时时间
         */
        int CONNECT_TIMEOUT = 5 * 1000;
        /**
         * json content type
         */
        String CONTENT_TYPE_JSON = "application/json";
    }
}
