package com.sms.service;

import com.sms.dao.ClazzDao;
import com.sms.entity.Clazz;

import java.util.List;

/**
 * 班级业务
 */
public class ClazzService {

    private final ClazzDao clazzDao = new ClazzDao();

    public List<Clazz> list() {
        return clazzDao.findAll();
    }

    public Clazz get(Long id) {
        return clazzDao.findById(id);
    }

    public String add(Clazz cl) {
        if (cl.getName() == null || cl.getName().trim().isEmpty()) return "班级名称不能为空";
        clazzDao.insert(cl);
        return null;
    }

    public String update(Clazz cl) {
        if (cl.getName() == null || cl.getName().trim().isEmpty()) return "班级名称不能为空";
        clazzDao.update(cl);
        return null;
    }

    public void delete(Long id) {
        clazzDao.delete(id);
    }
}
