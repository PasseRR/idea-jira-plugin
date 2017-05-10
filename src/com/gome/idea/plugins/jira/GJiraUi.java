package com.gome.idea.plugins.jira;

import javax.swing.*;

/**
 * ui接口
 * @author xiehai1
 * @date 2017/05/10 14:06
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public interface GJiraUi {
    /**
     * 获得ui的component
     * @return
     */
    JComponent getRootComponent();

    /**
     * 是否修改
     * @return
     */
    boolean isModify();

    /**
     * 重置设置
     */
    void reset();
}
