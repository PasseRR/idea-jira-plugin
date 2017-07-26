package com.gome.idea.plugins.jira.toolwindow;

import com.gome.idea.plugins.jira.util.JiraTimeFormatUtil;
import com.gome.idea.plugins.jira.vo.IssueVo;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * issure tree 渲染器
 *
 * @author xiehai1
 * @date 2017/05/10 21:08
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class GJiraDefaultTreeCellRenderer extends DefaultTreeCellRenderer {
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        if (leaf && row != 0) {
            IssueVo issueVo = (IssueVo) ((DefaultMutableTreeNode) value).getUserObject();
            StringBuilder sb = new StringBuilder();
            sb.append(issueVo.getKey());
            sb.append(" ");
            sb.append(issueVo.getStatus());
            sb.append(" ");
            sb.append(issueVo.getSummary());
            sb.append(" ");
            sb.append(JiraTimeFormatUtil.formatTime(issueVo.getTimeOriginalEstimate()) + "d");
            sb.append(" ");
            sb.append(JiraTimeFormatUtil.formatTime(issueVo.getTimespent()) + "d");
            this.setText(sb.toString());
            if(null == issueVo.getTimespent()){ // 未更新时间的标红
                this.setForeground(Color.RED);
            }
            if(issueVo.getTimespent() != null
                && !issueVo.getTimespent().equals(issueVo.getTimeOriginalEstimate())){
               this.setForeground(Color.MAGENTA);
            }

            if (issueVo.getIssueType().contains("测试")) {
                this.setIcon(new ImageIcon(this.getClass().getResource("/icon/bug.png")));
            } else {
                this.setIcon(new ImageIcon(this.getClass().getResource("/icon/task.png")));
            }
        } else {
            this.setText(value.toString());
            this.setIcon(new ImageIcon(this.getClass().getResource("/icon/list.png")));
        }

        return this;
    }
}
