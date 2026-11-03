package com.sms.service;

import com.sms.dao.CourseDao;
import com.sms.entity.Course;
import com.sms.entity.CourseClass;

import java.util.List;

/**
 * 课程业务（含课程-班级绑定）
 */
public class CourseService {

    private final CourseDao courseDao = new CourseDao();

    public List<Course> list() {
        return courseDao.findAll();
    }

    public List<Course> listByTeacher(Long teacherId) {
        return courseDao.findByTeacher(teacherId);
    }

    public List<Course> listByClass(Long classId) {
        return courseDao.findByClass(classId);
    }

    public String add(Course c) {
        if (c.getName() == null || c.getName().trim().isEmpty()) return "课程名称不能为空";
        courseDao.insert(c);
        return null;
    }

    public String update(Course c) {
        if (c.getName() == null || c.getName().trim().isEmpty()) return "课程名称不能为空";
        courseDao.update(c);
        return null;
    }

    public void delete(Long id) {
        courseDao.delete(id);
    }

    public List<CourseClass> listClasses(Long courseId) {
        return courseDao.listClassesByCourse(courseId);
    }

    public void bindClass(Long courseId, Long classId) {
        courseDao.bindClass(courseId, classId);
    }

    public void unbindClass(Long courseId, Long classId) {
        courseDao.unbindClass(courseId, classId);
    }
}
