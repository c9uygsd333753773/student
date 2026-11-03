package com.sms.service;

import com.sms.dao.ClazzDao;
import com.sms.dao.CourseDao;
import com.sms.dao.TeacherDao;
import com.sms.dao.UserDao;
import com.sms.entity.Teacher;
import com.sms.entity.User;
import com.sms.util.MD5Util;

import java.util.List;

/**
 * 教师档案业务（管理员管理教师）
 */
public class TeacherService {

    private final TeacherDao teacherDao = new TeacherDao();
    private final UserDao userDao = new UserDao();
    private final CourseDao courseDao = new CourseDao();
    private final ClazzDao clazzDao = new ClazzDao();

    public List<Teacher> list() {
        return teacherDao.findAll();
    }

    /** 新增教师（同时创建登录账号）；返回错误信息，成功返回 null */
    public String add(Teacher t, String username, String password) {
        if (userDao.findByUsername(username) != null) return "用户名已存在";
        User u = new User();
        u.setUsername(username);
        u.setPassword(MD5Util.md5(password));
        u.setRole("TEACHER");
        u.setName(t.getName());
        u.setPhone(t.getPhone());
        Long userId = userDao.insert(u);
        t.setUserId(userId);
        teacherDao.insert(t);
        return null;
    }

    public String update(Teacher t) {
        if (t.getName() == null || t.getName().trim().isEmpty()) return "姓名不能为空";
        teacherDao.update(t);
        return null;
    }

    public void delete(Long id) {
        Teacher t = teacherDao.findById(id);
        if (t != null) {
            courseDao.clearTeacher(id);
            clazzDao.clearHeadTeacher(id);
            teacherDao.delete(id);
            userDao.delete(t.getUserId());
        }
    }
}
