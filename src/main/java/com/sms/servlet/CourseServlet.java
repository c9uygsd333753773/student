package com.sms.servlet;

import com.sms.entity.Course;
import com.sms.service.CourseService;
import com.sms.util.Result;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 课程管理（含课程-班级绑定）
 */
@WebServlet("/course")
public class CourseServlet extends BaseServlet {

    private final CourseService courseService = new CourseService();

    /** 全部课程（管理员） */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        writeJson(resp, Result.ok(courseService.list()));
    }

    /** 教师自己的课程 */
    public void listByTeacher(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "TEACHER");
        writeJson(resp, Result.ok(courseService.listByTeacher(getProfileId(req))));
    }

    /** 学生本班课程 */
    public void listByClass(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "STUDENT");
        Long classId = longParam(req, "classId");
        if (classId == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        writeJson(resp, Result.ok(courseService.listByClass(classId)));
    }

    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        Course c = build(req);
        String err = courseService.add(c);
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
        Course c = build(req);
        c.setId(id);
        String err = courseService.update(c);
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
        courseService.delete(id);
        writeJson(resp, Result.ok("已删除"));
    }

    /** 课程已绑定的班级 */
    public void listClasses(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        Long courseId = longParam(req, "courseId");
        if (courseId == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        writeJson(resp, Result.ok(courseService.listClasses(courseId)));
    }

    public void bindClass(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        Long courseId = longParam(req, "courseId");
        Long classId = longParam(req, "classId");
        if (courseId == null || classId == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        courseService.bindClass(courseId, classId);
        writeJson(resp, Result.ok("绑定成功"));
    }

    public void unbindClass(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        Long courseId = longParam(req, "courseId");
        Long classId = longParam(req, "classId");
        if (courseId == null || classId == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        courseService.unbindClass(courseId, classId);
        writeJson(resp, Result.ok("解绑成功"));
    }

    private Course build(HttpServletRequest req) {
        Course c = new Course();
        c.setName(param(req, "name"));
        c.setCredit(doubleParam(req, "credit"));
        c.setTeacherId(longParam(req, "teacherId"));
        return c;
    }
}
