package com.sms.service;

import com.sms.dao.NoticeDao;
import com.sms.entity.Notice;

import java.util.List;

/**
 * 公告通知业务
 */
public class NoticeService {

    private final NoticeDao noticeDao = new NoticeDao();

    public List<Notice> list() {
        return noticeDao.findAll();
    }

    public List<Notice> listVisibleForStudent(Long classId) {
        return noticeDao.findVisibleForStudent(classId);
    }

    public List<Notice> listMine(Long publisherId, String role) {
        return noticeDao.findByPublisher(publisherId, role);
    }

    public String add(Notice n) {
        if (n.getTitle() == null || n.getTitle().trim().isEmpty()) return "标题不能为空";
        if (n.getContent() == null || n.getContent().trim().isEmpty()) return "内容不能为空";
        noticeDao.insert(n);
        return null;
    }

    public void delete(Long id) {
        noticeDao.delete(id);
    }
}
