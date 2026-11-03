package com.sms.entity;

/**
 * 教学资源（课件）实体
 */
public class Resource {
    private Long id;
    private Long teacherId;
    private Long courseId;
    private String filename;    // 原始文件名
    private String storedName;  // 存储文件名
    private String path;        // 相对路径
    private String description;
    private String createdAt;

    // 关联展示字段
    private String teacherName;
    private String courseName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public String getStoredName() { return storedName; }
    public void setStoredName(String storedName) { this.storedName = storedName; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getTeacherName() { return teacherName; }
    public void setTeacherName(String teacherName) { this.teacherName = teacherName; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
}
