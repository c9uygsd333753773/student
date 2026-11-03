package com.sms.servlet;

import com.sms.dao.ClazzDao;
import com.sms.entity.Clazz;
import com.sms.service.ClazzService;
import com.sms.util.Result;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 班级管理
 */
@WebServlet("/clazz")
public class ClazzServlet extends BaseServlet {

    private final ClazzService clazzService = new ClazzService();
    private final ClazzDao clazzDao = new ClazzDao();

    /** 全部班级（公开，供注册页等下拉使用；名单信息不敏感） */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        writeJson(resp, Result.ok(clazzService.list()));
    }

    /** 教师视角：自己的班级 */
    public void listByTeacher(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "TEACHER");
        writeJson(resp, Result.ok(clazzDao.findByTeacher(getProfileId(req))));
    }

    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        Clazz cl = build(req);
        String err = clazzService.add(cl);
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
        Clazz cl = build(req);
        cl.setId(id);
        String err = clazzService.update(cl);
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
        clazzService.delete(id);
        writeJson(resp, Result.ok("已删除"));
    }

    private Clazz build(HttpServletRequest req) {
        Clazz cl = new Clazz();
        cl.setName(param(req, "name"));
        cl.setGrade(param(req, "grade"));
        cl.setHeadTeacherId(longParam(req, "headTeacherId"));
        return cl;
    }
}
