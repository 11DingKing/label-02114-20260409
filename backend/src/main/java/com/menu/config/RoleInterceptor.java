package com.menu.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * RBAC 角色拦截器：检查 @AdminRequired 注解
 */
@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 检查方法或类上是否有 @AdminRequired
        boolean adminRequired = handlerMethod.hasMethodAnnotation(AdminRequired.class)
                || handlerMethod.getBeanType().isAnnotationPresent(AdminRequired.class);

        if (!adminRequired) {
            return true;
        }

        Integer role = (Integer) request.getAttribute("role");
        if (role == null || role != 1) {
            response.setStatus(403);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":403,\"message\":\"权限不足，需要管理员权限\"}");
            return false;
        }
        return true;
    }
}
