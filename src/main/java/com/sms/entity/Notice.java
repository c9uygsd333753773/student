package com.sms.entity;

/**
 * 公告通知实体
 */
public class Notice {
    private Long id;
    private String title;
    private String content;
    private Long publisherId;
    private String publisherRole;
    private String scope;     // ALL / CLASS
    private Long classId;
    private String createdAt;

    // 关联展示字段
    private String publisherName;
    private String className;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Long getPublisherId() { return publisherId; }
    public void setPublisherId(Long publisherId) { this.publisherId = publisherId; }
    public String getPublisherRole() { return publisherRole; }
    public void setPublisherRole(String publisherRole) { this.publisherRole = publisherRole; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
