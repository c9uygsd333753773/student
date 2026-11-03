package com.sms.filter;

import com.sms.entity.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 登录与角色权限过滤器：保护 /admin、/teacher、/student 下的页面。
 */
@WebFilter(urlPatterns = {"/admin/*", "/teacher/*", "/student/*"})
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        User user = (User) req.getSession().getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        String uri = req.getRequestURI();
        String role = user.getRole();
        boolean allowed = false;
        if (uri.contains("/admin/") && "ADMIN".equals(role)) allowed = true;
        else if (uri.contains("/teacher/") && "TEACHER".equals(role)) allowed = true;
        else if (uri.contains("/student/") && "STUDENT".equals(role)) allowed = true;

        if (!allowed) {
            resp.sendRedirect(req.getContextPath() + "/login.html");
            return;
        }

        chain.doFilter(request, response);
    }
}
