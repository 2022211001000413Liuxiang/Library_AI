package com.library.config;

import com.library.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    // 存放用户信息的 key
    public static final String USER_ID = "userId";
    public static final String USER_NAME = "username";
    public static final String USER_ROLE = "role";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行预检请求
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        // 从请求头获取 token
        String token = request.getHeader("Authorization");

        // 如果没有 token，返回 401
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"未登录或token已过期\"}");
            return false;
        }

        // 去掉 "Bearer " 前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // 验证 token
        if (!JwtUtils.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"token已过期，请重新登录\"}");
            return false;
        }

        // 将用户信息存入请求属性，供后续使用
        Long userId = JwtUtils.getUserIdFromToken(token);
        String username = JwtUtils.getUsernameFromToken(token);
        String role = JwtUtils.getRoleFromToken(token);

        request.setAttribute(USER_ID, userId);
        request.setAttribute(USER_NAME, username);
        request.setAttribute(USER_ROLE, role);

        return true;
    }
}
