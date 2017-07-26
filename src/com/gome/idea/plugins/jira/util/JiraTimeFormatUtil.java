package com.gome.idea.plugins.jira.util;

import java.math.BigDecimal;

/**
 * @author xiehai1
 * @date 2017/07/26 11:10
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public final class JiraTimeFormatUtil {
    private JiraTimeFormatUtil(){

    }

    public static BigDecimal formatTime(Long time){
        if (time == null){
            return new BigDecimal(0);
        }
        BigDecimal bigDecimal = new BigDecimal(time);
        bigDecimal.setScale(2);
        return bigDecimal.divide(new BigDecimal(8 * 60 * 60));
    }
}
