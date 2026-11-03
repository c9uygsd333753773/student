package com.sms.servlet;

import com.sms.dao.StudentDao;
import com.sms.dao.TeacherDao;
import com.sms.entity.Student;
import com.sms.entity.Teacher;
import com.sms.entity.User;
import com.sms.service.StudentService;
import com.sms.service.UserService;
import com.sms.util.Result;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 账号相关：登录、注册、退出、密码、资料、账号管理
 */
@WebServlet("/user")
public class UserServlet extends BaseServlet {

    private final UserService userService = new UserService();
    private final StudentService studentService = new StudentService();
    private final TeacherDao teacherDao = new TeacherDao();
    private final StudentDao studentDao = new StudentDao();

    /** 登录 */
    public void login(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = param(req, "username");
        String password = param(req, "password");
        if (username == null || password == null) {
            writeJson(resp, Result.fail("用户名和密码不能为空"));
            return;
        }
        User user = userService.login(username, password);
        if (user == null) {
            writeJson(resp, Result.fail("用户名或密码错误"));
            return;
        }
        HttpSession session = req.getSession();
        session.setAttribute("user", user);
        session.setAttribute("role", user.getRole());

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("role", user.getRole());
        data.put("name", user.getName());
        data.put("userId", user.getId());
        if ("TEACHER".equals(user.getRole())) {
            Teacher t = teacherDao.findByUserId(user.getId());
            if (t != null) {
                session.setAttribute("profileId", t.getId());
                data.put("profileId", t.getId());
            }
        } else if ("STUDENT".equals(user.getRole())) {
            Student s = studentDao.findByUserId(user.getId());
            if (s != null) {
                session.setAttribute("profileId", s.getId());
                data.put("profileId", s.getId());
            }
        }
        writeJson(resp, Result.ok("登录成功", data));
    }

    /** 注册（默认注册为学生账号） */
    public void register(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String username = param(req, "username");
        String password = param(req, "password");
        String name = param(req, "name");
        String studentNo = param(req, "studentNo");
        if (username == null || password == null || name == null || studentNo == null) {
            writeJson(resp, Result.fail("用户名、密码、姓名、学号均不能为空"));
            return;
        }
        Student s = new Student();
        s.setStudentNo(studentNo);
        s.setName(name);
        s.setGender(param(req, "gender"));
        s.setPhone(param(req, "phone"));
        s.setAddress(param(req, "address"));
        s.setClassId(longParam(req, "classId"));
        s.setEnrollmentYear(param(req, "enrollmentYear"));
        String err = studentService.add(s, username, password);
        if (err != null) {
            writeJson(resp, Result.fail(err));
            return;
        }
        writeJson(resp, Result.ok("注册成功，请登录"));
    }

    /** 退出登录 */
    public void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.getSession().invalidate();
        writeJson(resp, Result.ok("已退出"));
    }

    /** 当前登录用户信息 */
    public void info(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = getLoginUser(req);
        if (u == null) {
            writeJson(resp, Result.fail("未登录"));
            return;
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", u.getId());
        data.put("username", u.getUsername());
        data.put("name", u.getName());
        data.put("role", u.getRole());
        data.put("email", u.getEmail());
        data.put("phone", u.getPhone());
        writeJson(resp, Result.ok(data));
    }

    /** 修改个人资料 */
    public void updateProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = requireLogin(req);
        u.setName(param(req, "name"));
        u.setEmail(param(req, "email"));
        u.setPhone(param(req, "phone"));
        String err = userService.updateProfile(u);
        if (err != null) {
            writeJson(resp, Result.fail(err));
            return;
        }
        // 同步更新 session 中的用户信息
        req.getSession().setAttribute("user", u);
        writeJson(resp, Result.ok("资料已更新"));
    }

    /** 修改密码 */
    public void updatePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = requireLogin(req);
        String oldPwd = param(req, "oldPwd");
        String newPwd = param(req, "newPwd");
        String err = userService.updatePassword(u.getId(), oldPwd, newPwd);
        if (err != null) {
            writeJson(resp, Result.fail(err));
            return;
        }
        writeJson(resp, Result.ok("密码已修改"));
    }

    /** 管理员：账号列表 */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        String role = param(req, "role");
        writeJson(resp, Result.ok(userService.listByRole(role)));
    }

    /** 管理员：删除账号 */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        Long id = longParam(req, "id");
        String role = param(req, "role");
        if (id == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        userService.deleteAccount(id, role);
        writeJson(resp, Result.ok("已删除"));
    }

    /** 管理员：重置密码为 123456 */
    public void resetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        Long id = longParam(req, "id");
        if (id == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        userService.resetPassword(id);
        writeJson(resp, Result.ok("密码已重置为 123456"));
    }
}
