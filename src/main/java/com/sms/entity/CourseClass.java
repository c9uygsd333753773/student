package com.sms.entity;

/**
 * 课程-班级关联实体
 */
public class CourseClass {
    private Long id;
    private Long courseId;
    private Long classId;

    // 关联展示字段
    private String courseName;
    private String className;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCourseId() { return courseId; }
    public void setCourseId(Long courseId) { this.courseId = courseId; }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
}
