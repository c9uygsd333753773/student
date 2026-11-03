package com.sms.service;

import com.sms.dao.ResourceDao;
import com.sms.entity.Resource;

import java.util.List;

/**
 * 教学资源业务
 */
public class ResourceService {

    private final ResourceDao resourceDao = new ResourceDao();

    public List<Resource> list() {
        return resourceDao.findAll();
    }

    public List<Resource> listByTeacher(Long teacherId) {
        return resourceDao.findByTeacher(teacherId);
    }

    public Resource get(Long id) {
        return resourceDao.findById(id);
    }

    public Long add(Resource r) {
        return resourceDao.insert(r);
    }

    public void delete(Long id) {
        resourceDao.delete(id);
    }
}
