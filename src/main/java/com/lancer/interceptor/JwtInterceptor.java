package com.lancer.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lancer.annotation.RequireAdmin;
import com.lancer.common.result.Result;
import com.lancer.entity.User;
import com.lancer.mapper.UserMapper;
import com.lancer.utils.JwtUtils;
import com.lancer.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    public JwtInterceptor(JwtUtils jwtUtils, UserMapper userMapper, ObjectMapper objectMapper) {
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行非方法请求（如静态资源）
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // OPTIONS 预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        if (isPublicReadEndpoint(request)) {
            return true;
        }

        // 获取 Token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendError(response, 401, "未登录，请先登录");
            return false;
        }

        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtUtils.parseToken(token);
        } catch (Exception exception) {
            sendError(response, 401, "登录已过期，请重新登录");
            return false;
        }

        Long userId;
        try {
            userId = Long.parseLong(claims.getSubject());
        } catch (NumberFormatException exception) {
            sendError(response, 401, "登录凭证无效，请重新登录");
            return false;
        }

        // 权限以数据库中的当前状态为准，避免长期 Token 携带过期角色。
        User user = userMapper.selectById(userId);
        if (user == null) {
            sendError(response, 401, "用户不存在或已被停用");
            return false;
        }
        user.setPassword(null);
        UserContext.setCurrentUser(user);

        // 检查管理员权限
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireAdmin requireAdmin = handlerMethod.getMethodAnnotation(RequireAdmin.class);
        if (requireAdmin == null) {
            requireAdmin = handlerMethod.getBeanType().getAnnotation(RequireAdmin.class);
        }
        if (requireAdmin != null && !Integer.valueOf(1).equals(user.getRole())) {
            UserContext.clear();
            sendError(response, 403, "权限不足，仅管理员可操作");
            return false;
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void sendError(HttpServletResponse response, int code, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(code == 401 ? 401 : 403);
        Result<?> result = Result.error(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }

    private boolean isPublicReadEndpoint(HttpServletRequest request) {
        if (!"GET".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String path = request.getRequestURI().substring(request.getContextPath().length());
        return "/api/animals".equals(path)
                || path.matches("/api/animals/\\d+")
                || path.matches("/api/animals/\\d+/checkins");
    }
}
