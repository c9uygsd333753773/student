package com.sms.servlet;

import com.sms.entity.Teacher;
import com.sms.service.TeacherService;
import com.sms.util.Result;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 教师档案：管理员管理教师账号与档案
 */
@WebServlet("/teacher")
public class TeacherServlet extends BaseServlet {

    private final TeacherService teacherService = new TeacherService();

    public void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        writeJson(resp, Result.ok(teacherService.list()));
    }

    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        Teacher t = buildTeacher(req);
        String username = param(req, "username");
        String password = param(req, "password");
        if (username == null || password == null) {
            writeJson(resp, Result.fail("用户名和密码不能为空"));
            return;
        }
        String err = teacherService.add(t, username, password);
        if (err != null) {
            writeJson(resp, Result.fail(err));
            return;
        }
        writeJson(resp, Result.ok("新增成功"));
    }

    public void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        Long id = longParam(req, "id");
        if (id == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        Teacher t = buildTeacher(req);
        t.setId(id);
        String err = teacherService.update(t);
        if (err != null) {
            writeJson(resp, Result.fail(err));
            return;
        }
        writeJson(resp, Result.ok("修改成功"));
    }

    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        Long id = longParam(req, "id");
        if (id == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        teacherService.delete(id);
        writeJson(resp, Result.ok("已删除"));
    }

    private Teacher buildTeacher(HttpServletRequest req) {
        Teacher t = new Teacher();
        t.setTeacherNo(param(req, "teacherNo"));
        t.setName(param(req, "name"));
        t.setGender(param(req, "gender"));
        t.setPhone(param(req, "phone"));
        t.setTitle(param(req, "title"));
        return t;
    }
}
