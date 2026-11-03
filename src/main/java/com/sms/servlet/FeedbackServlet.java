package com.sms.servlet;

import com.sms.entity.Feedback;
import com.sms.entity.User;
import com.sms.service.FeedbackService;
import com.sms.util.Result;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 反馈留言：学生提交，管理员/教师回复
 */
@WebServlet("/feedback")
public class FeedbackServlet extends BaseServlet {

    private final FeedbackService feedbackService = new FeedbackService();

    /** 学生：我的留言 */
    public void listMy(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "STUDENT");
        writeJson(resp, Result.ok(feedbackService.listByStudent(getProfileId(req))));
    }

    /** 管理员/教师：全部留言 */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN", "TEACHER");
        writeJson(resp, Result.ok(feedbackService.list()));
    }

    /** 学生：提交留言 */
    public void add(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "STUDENT");
        Feedback f = new Feedback();
        f.setStudentId(getProfileId(req));
        f.setContent(param(req, "content"));
        String err = feedbackService.add(f);
        if (err != null) {
            writeJson(resp, Result.fail(err));
            return;
        }
        writeJson(resp, Result.ok("留言已提交"));
    }

    /** 管理员/教师：回复留言 */
    public void reply(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "ADMIN", "TEACHER");
        Long id = longParam(req, "id");
        String reply = param(req, "reply");
        if (id == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        String err = feedbackService.reply(id, reply);
        if (err != null) {
            writeJson(resp, Result.fail(err));
            return;
        }
        writeJson(resp, Result.ok("回复成功"));
    }

    /** 删除留言 */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = requireLogin(req);
        Long id = longParam(req, "id");
        if (id == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        // 管理员/教师可删，学生可删自己的留言
        if (!"ADMIN".equals(u.getRole()) && !"TEACHER".equals(u.getRole())) {
            requireRole(req, "STUDENT");
        }
        feedbackService.delete(id);
        writeJson(resp, Result.ok("已删除"));
    }
}
