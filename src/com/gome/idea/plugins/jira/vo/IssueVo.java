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
    // 预估时间
    private Long timeOriginalEstimate;
    // 已经消耗时间
    private Long timespent;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public Long getTimeOriginalEstimate() {
        return timeOriginalEstimate;
    }

    public void setTimeOriginalEstimate(Long timeOriginalEstimate) {
        this.timeOriginalEstimate = timeOriginalEstimate;
    }

    public Long getTimespent() {
        return timespent;
    }

    public void setTimespent(Long timespent) {
        this.timespent = timespent;
    }

    @Override
    public String toString() {
        return "IssueVo{" +
            "key='" + key + '\'' +
            ", issueType='" + issueType + '\'' +
            ", summary='" + summary + '\'' +
            ", status='" + status + '\'' +
            ", timeOriginalEstimate=" + timeOriginalEstimate +
            ", timespent=" + timespent +
            '}';
    }
}
