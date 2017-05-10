package com.gome.idea.plugins.jira.vo;

/**
 * jira任务vo
 * @author xiehai1
 * @date 2017/05/10 19:29
 * @Copyright(c) gome inc Gome Co.,LTD
 */
public class IssueVo {
    // ticket编号
    private String key;
    // issue类型
    private String issueType;
    // 标题
    private String summary;
    // 状态
    private String status;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getIssuetype() {
        return issueType;
    }

    public void setIssuetype(String issuetype) {
        this.issueType = issuetype;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "IssueVo{" +
                "key='" + key + '\'' +
                ", issueType='" + issueType + '\'' +
                ", summary='" + summary + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
