package com.sms.service;

import com.sms.dao.TeacherDao;
import com.sms.dao.UserDao;
import com.sms.entity.User;
import com.sms.util.MD5Util;

import java.util.List;

/**
 * 账号业务：登录、注册、资料修改、密码、删除
 */
public class UserService {

    private final UserDao userDao = new UserDao();
    private final TeacherDao teacherDao = new TeacherDao();

    public User login(String username, String password) {
        User u = userDao.findByUsername(username);
        if (u == null) return null;
        if (!u.getPassword().equals(MD5Util.md5(password))) return null;
        // 密码不回传前端
        u.setPassword(null);
        return u;
    }

    public boolean exists(String username) {
        return userDao.findByUsername(username) != null;
    }

    /** 修改资料；返回错误信息，成功返回 null */
    public String updateProfile(User u) {
        if (u.getName() == null || u.getName().trim().isEmpty()) return "姓名不能为空";
        userDao.updateProfile(u);
        return null;
    }

    /** 修改密码；返回错误信息，成功返回 null */
    public String updatePassword(Long userId, String oldPwd, String newPwd) {
        User u = userDao.findById(userId);
        if (u == null) return "账号不存在";
        if (!u.getPassword().equals(MD5Util.md5(oldPwd))) return "原密码错误";
        if (newPwd == null || newPwd.length() < 6) return "新密码长度不能少于 6 位";
        userDao.updatePassword(userId, MD5Util.md5(newPwd));
        return null;
    }

    /** 管理员重置密码为 123456 */
    public void resetPassword(Long userId) {
        userDao.updatePassword(userId, MD5Util.md5("123456"));
    }

    public List<User> listByRole(String role) {
        if (role == null || role.isEmpty()) return userDao.findAll();
        return userDao.findByRole(role);
    }

    /** 删除账号（并清理关联的教师档案） */
    public void deleteAccount(Long userId, String role) {
        if ("TEACHER".equals(role)) teacherDao.deleteByUserId(userId);
        userDao.delete(userId);
    }
}
