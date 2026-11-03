package com.sms.servlet;

import com.google.gson.Gson;
import com.sms.entity.User;
import com.sms.util.Result;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 基础 Servlet：通过 method 参数反射分发到具体处理方法，统一 JSON 输出与异常处理。
 * 子类方法签名统一为 public void xxx(HttpServletRequest, HttpServletResponse)。
 */
public class BaseServlet extends HttpServlet {

    private static final Gson GSON = new Gson();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String method = req.getParameter("method");
        if (method == null || method.trim().isEmpty()) method = "index";
        try {
            Method m = this.getClass().getMethod(method, HttpServletRequest.class, HttpServletResponse.class);
            m.invoke(this, req, resp);
        } catch (Exception e) {
            Throwable cause = (e.getCause() != null) ? e.getCause() : e;
            writeJson(resp, Result.fail("服务器异常: " + cause.getMessage()));
        }
    }

    // ===== 公共辅助方法 =====

    protected User getLoginUser(HttpServletRequest req) {
        return (User) req.getSession().getAttribute("user");
    }

    protected User requireLogin(HttpServletRequest req) {
        User u = getLoginUser(req);
        if (u == null) throw new RuntimeException("未登录或会话已过期");
        return u;
    }

    protected void requireRole(HttpServletRequest req, String... roles) {
        User u = requireLogin(req);
        for (String r : roles) {
            if (r.equals(u.getRole())) return;
        }
        throw new RuntimeException("无权限执行该操作");
    }

    /** 教师/学生的档案 id（登录时写入 session） */
    protected Long getProfileId(HttpServletRequest req) {
        Object o = req.getSession().getAttribute("profileId");
        return o == null ? null : Long.valueOf(String.valueOf(o));
    }

    protected String param(HttpServletRequest req, String name) {
        String v = req.getParameter(name);
        return (v == null || v.trim().isEmpty()) ? null : v.trim();
    }

    protected Long longParam(HttpServletRequest req, String name) {
        String v = param(req, name);
        if (v == null) return null;
        try {
            return Long.valueOf(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected Double doubleParam(HttpServletRequest req, String name) {
        String v = param(req, name);
        if (v == null) return null;
        try {
            return Double.valueOf(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    protected void writeJson(HttpServletResponse resp, Object obj) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(GSON.toJson(obj));
    }
}
