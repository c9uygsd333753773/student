package com.sms.servlet;

import com.sms.entity.Student;
import com.sms.service.StudentService;
import com.sms.util.Result;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 学生档案：管理员管理全部，教师查看授课班级学生，学生查看/修改自己
 */
@WebServlet("/student")
public class StudentServlet extends BaseServlet {

    private final StudentService studentService = new StudentService();

    /** 管理员：全部学生 */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        writeJson(resp, Result.ok(studentService.list()));
    }

    /** 按班级查学生（管理员/教师） */
    public void listByClass(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN", "TEACHER");
        Long classId = longParam(req, "classId");
        if (classId == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        writeJson(resp, Result.ok(studentService.listByClass(classId)));
    }

    /** 教师：自己授课/带班班级的学生 */
    public void listByTeacher(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "TEACHER");
        Long teacherId = getProfileId(req);
        writeJson(resp, Result.ok(studentService.listByTeacher(teacherId)));
    }

    /** 当前学生自己的档案 */
    public void myProfile(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "STUDENT");
        Long studentId = getProfileId(req);
        writeJson(resp, Result.ok(studentService.getById(studentId)));
    }

    /** 管理员：新增学生（同时创建账号） */
    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        Student s = buildStudent(req);
        String username = param(req, "username");
        String password = param(req, "password");
        if (username == null || password == null) {
            writeJson(resp, Result.fail("用户名和密码不能为空"));
            return;
        }
        String err = studentService.add(s, username, password);
        if (err != null) {
            writeJson(resp, Result.fail(err));
            return;
        }
        writeJson(resp, Result.ok("新增成功"));
    }

    /** 管理员/学生本人：修改学生档案 */
    public void update(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long studentId = longParam(req, "id");
        String role = requireLogin(req).getRole();
        if ("STUDENT".equals(role)) {
            studentId = getProfileId(req);
        } else {
            requireRole(req, "ADMIN", "TEACHER");
        }
        if (studentId == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        Student s = buildStudent(req);
        s.setId(studentId);
        String err = studentService.update(s);
        if (err != null) {
            writeJson(resp, Result.fail(err));
            return;
        }
        writeJson(resp, Result.ok("修改成功"));
    }

    /** 管理员：删除学生 */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN");
        Long id = longParam(req, "id");
        if (id == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        studentService.delete(id);
        writeJson(resp, Result.ok("已删除"));
    }

    private Student buildStudent(HttpServletRequest req) {
        Student s = new Student();
        s.setStudentNo(param(req, "studentNo"));
        s.setName(param(req, "name"));
        s.setGender(param(req, "gender"));
        s.setPhone(param(req, "phone"));
        s.setAddress(param(req, "address"));
        s.setClassId(longParam(req, "classId"));
        s.setEnrollmentYear(param(req, "enrollmentYear"));
        return s;
    }
}
