package com.sms.servlet;

import com.sms.entity.Resource;
import com.sms.entity.User;
import com.sms.service.ResourceService;
import com.sms.util.Result;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * 教学资源（课件）上传 / 列表 / 删除 / 下载
 */
@WebServlet("/resource")
@MultipartConfig(maxFileSize = 50 * 1024 * 1024, maxRequestSize = 60 * 1024 * 1024)
public class ResourceServlet extends BaseServlet {

    private final ResourceService resourceService = new ResourceService();

    /** 资源列表（教师看自己的，管理员看全部，学生看全部） */
    public void list(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User u = requireLogin(req);
        if ("TEACHER".equals(u.getRole())) {
            writeJson(resp, Result.ok(resourceService.listByTeacher(getProfileId(req))));
        } else {
            writeJson(resp, Result.ok(resourceService.list()));
        }
    }

    /** 教师：上传课件 */
    public void upload(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "TEACHER");
        try {
            Part filePart = req.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                writeJson(resp, Result.fail("请选择要上传的文件"));
                return;
            }
            String filename = filePart.getSubmittedFileName();
            if (filename == null || filename.isEmpty()) {
                writeJson(resp, Result.fail("文件名无效"));
                return;
            }
            // 存储目录：webapp/uploads
            String uploadDir = req.getServletContext().getRealPath("/uploads");
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String ext = "";
            int dot = filename.lastIndexOf('.');
            if (dot >= 0) ext = filename.substring(dot);
            String storedName = UUID.randomUUID().toString().replace("-", "") + ext;
            filePart.write(uploadDir + File.separator + storedName);

            Resource r = new Resource();
            r.setTeacherId(getProfileId(req));
            r.setCourseId(longParam(req, "courseId"));
            r.setFilename(filename);
            r.setStoredName(storedName);
            r.setPath("/uploads/" + storedName);
            r.setDescription(param(req, "description"));
            resourceService.add(r);
            writeJson(resp, Result.ok("上传成功"));
        } catch (Exception e) {
            writeJson(resp, Result.fail("上传失败: " + e.getMessage()));
        }
    }

    /** 删除资源 */
    public void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        requireRole(req, "TEACHER", "ADMIN");
        Long id = longParam(req, "id");
        if (id == null) {
            writeJson(resp, Result.fail("参数错误"));
            return;
        }
        Resource r = resourceService.get(id);
        if (r != null) {
            // 删除物理文件
            try {
                String realPath = req.getServletContext().getRealPath(r.getPath());
                if (realPath != null) {
                    File f = new File(realPath);
                    if (f.exists()) f.delete();
                }
            } catch (Exception ignored) {}
        }
        resourceService.delete(id);
        writeJson(resp, Result.ok("已删除"));
    }
}
