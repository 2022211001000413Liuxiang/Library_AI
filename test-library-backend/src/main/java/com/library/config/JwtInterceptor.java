package com.library.config;

import com.library.annotation.RequireRole;
import com.library.utils.JwtUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    public static final String USER_ID = "userId";
    public static final String USER_NAME = "username";
    public static final String USER_ROLE = "role";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"未登录或token已过期\"}");
            return false;
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!JwtUtils.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"token已过期，请重新登录\"}");
            return false;
        }

        Long userId = JwtUtils.getUserIdFromToken(token);
        String username = JwtUtils.getUsernameFromToken(token);
        String role = JwtUtils.getRoleFromToken(token);

        request.setAttribute(USER_ID, userId);
        request.setAttribute(USER_NAME, username);
        request.setAttribute(USER_ROLE, role);

        // --- 核心鉴权逻辑：检查方法上的 @RequireRole 注解 ---
        if (handler instanceof HandlerMethod) {
            // 1. 将处理器转换为 HandlerMethod，以便通过反射获取方法信息
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            
            // 2. 尝试从当前请求的方法上获取 @RequireRole 权限注解
            RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
            
            // 3. 如果方法上标注了该注解，则说明此接口有角色访问限制
            if (requireRole != null) {
                // 4. 获取注解中定义的允许访问的角色白名单（如：{"admin", "librarian"}）
                String[] allowedRoles = requireRole.value();
                
                // 5. 校验用户角色：
                //    - 如果 Token 中没有角色信息 (role == null)
                //    - 或者用户的角色不在白名单中 (noneMatch 返回 true)
                if (role == null || Arrays.stream(allowedRoles).noneMatch(r -> r.equals(role))) {
                    // 6. 鉴权失败，手动构造响应结果
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 设置 HTTP 状态码 403 (禁止访问)
                    response.setContentType("application/json;charset=UTF-8"); // 设置响应内容类型为 JSON
                    // 向前端返回统一的错误提示信息
                    response.getWriter().write("{\"success\":false,\"message\":\"权限不足，无法访问\"}");
                    
                    // 7. 返回 false 拦截请求，后续 Controller 中的业务逻辑将不再执行
                    return false;
                }
            }
        }

        return true;
    }
}
