package com.sms.servlet;

import com.sms.dao.StudentDao;
import com.sms.entity.Notice;
import com.sms.entity.Student;
import com.sms.entity.User;
import com.sms.service.NoticeService;
import com.sms.util.Result;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 公告通知：管理员/教师发布，所有人查看
 */
@WebServlet("/notice")
public class NoticeServlet extends BaseServlet {

    private final NoticeService noticeService = new NoticeService();
    private final StudentDao studentDao = new StudentDao();

    /** 可见公告：管理员/教师看全部；学生看全站 + 本班 */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = requireLogin(req);
        if ("STUDENT".equals(u.getRole())) {
            Student s = studentDao.findById(getProfileId(req));
            Long classId = s == null ? null : s.getClassId();
            if (classId == null) {
                writeJson(resp, Result.fail("未分配班级"));
                return;
            }
            writeJson(resp, Result.ok(noticeService.listVisibleForStudent(classId)));
        } else {
            writeJson(resp, Result.ok(noticeService.list()));
        }
    }

    /** 我发布的公告 */
    public void listMine(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = requireLogin(req);
        requireRole(req, "ADMIN", "TEACHER");
        writeJson(resp, Result.ok(noticeService.listMine(u.getId(), u.getRole())));
    }

    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = requireLogin(req);
        requireRole(req, "ADMIN", "TEACHER");
        Notice n = new Notice();
        n.setTitle(param(req, "title"));
        n.setContent(param(req, "content"));
        n.setScope(param(req, "scope"));
        if (n.getScope() == null) n.setScope("ALL");
        n.setClassId(longParam(req, "classId"));
        n.setPublisherId(u.getId());
        n.setPublisherRole(u.getRole());
        String err = noticeService.add(n);
        if (err != null) {
            writeJson(resp, Result.fail(err));
            return;
        }
        writeJson(resp, Result.ok("发布成功"));
    }

    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN", "TEACHER");
        Long id = longParam(req, "id");
        if (id == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        noticeService.delete(id);
        writeJson(resp, Result.ok("已删除"));
    }
}
