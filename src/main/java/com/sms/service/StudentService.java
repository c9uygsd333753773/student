package com.sms.service;

import com.sms.dao.CourseDao;
import com.sms.dao.FeedbackDao;
import com.sms.dao.ScoreDao;
import com.sms.dao.StudentDao;
import com.sms.dao.UserDao;
import com.sms.entity.Student;
import com.sms.entity.User;
import com.sms.util.MD5Util;

import java.util.List;

/**
 * 学生档案业务
 */
public class StudentService {

    private final StudentDao studentDao = new StudentDao();
    private final UserDao userDao = new UserDao();
    private final CourseDao courseDao = new CourseDao();
    private final ScoreDao scoreDao = new ScoreDao();
    private final FeedbackDao feedbackDao = new FeedbackDao();

    public List<Student> list() {
        return studentDao.findAll();
    }

    public List<Student> listByClass(Long classId) {
        return studentDao.findByClass(classId);
    }

    public Student getByUserId(Long userId) {
        return studentDao.findByUserId(userId);
    }

    public Student getById(Long id) {
        return studentDao.findById(id);
    }

    /** 教师视角：查看自己授课/带班班级的学生 */
    public List<Student> listByTeacher(Long teacherId) {
        List<Long> classIds = courseDao.findClassIdsByTeacher(teacherId);
        return studentDao.findByClassIds(classIds);
    }

    /** 新增学生（同时创建登录账号）；返回错误信息，成功返回 null */
    public String add(Student s, String username, String password) {
        if (userDao.findByUsername(username) != null) return "用户名已存在";
        if (studentDao.findByNo(s.getStudentNo()) != null) return "学号已存在";
        User u = new User();
        u.setUsername(username);
        u.setPassword(MD5Util.md5(password));
        u.setRole("STUDENT");
        u.setName(s.getName());
        u.setPhone(s.getPhone());
        Long userId = userDao.insert(u);
        s.setUserId(userId);
        studentDao.insert(s);
        return null;
    }

    public String update(Student s) {
        if (s.getName() == null || s.getName().trim().isEmpty()) return "姓名不能为空";
        studentDao.update(s);
        return null;
    }

    public void delete(Long id) {
        Student s = studentDao.findById(id);
        if (s != null) {
            scoreDao.deleteByStudent(id);
            feedbackDao.deleteByStudent(id);
            studentDao.delete(id);
            userDao.delete(s.getUserId());
        }
    }
}
