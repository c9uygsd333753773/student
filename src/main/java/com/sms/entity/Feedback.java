package com.sms.entity;

/**
 * 反馈留言实体
 */
public class Feedback {
    private Long id;
    private Long studentId;
    private String content;
    private String reply;
    private String status;      // UNREPLIED / REPLIED
    private String replyTime;
    private String createdAt;

    // 关联展示字段
    private String studentName;
    private String studentNo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReplyTime() { return replyTime; }
    public void setReplyTime(String replyTime) { this.replyTime = replyTime; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
}
