package com.sms.service;

import com.sms.dao.FeedbackDao;
import com.sms.entity.Feedback;

import java.util.List;

/**
 * 反馈留言业务
 */
public class FeedbackService {

    private final FeedbackDao feedbackDao = new FeedbackDao();

    public List<Feedback> list() {
        return feedbackDao.findAll();
    }

    public List<Feedback> listByStudent(Long studentId) {
        return feedbackDao.findByStudent(studentId);
    }

    public String add(Feedback f) {
        if (f.getContent() == null || f.getContent().trim().isEmpty()) return "留言内容不能为空";
        feedbackDao.insert(f);
        return null;
    }

    public String reply(Long id, String reply) {
        if (reply == null || reply.trim().isEmpty()) return "回复内容不能为空";
        feedbackDao.reply(id, reply);
        return null;
    }

    public void delete(Long id) {
        feedbackDao.delete(id);
    }
}
