package com.lancer.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lancer.annotation.RequireAdmin;
import com.lancer.common.result.Result;
import com.lancer.entity.User;
import com.lancer.utils.JwtUtils;
import com.lancer.utils.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 判断是否为公开接口（无需登录即可访问）
     */
    private boolean isPublicPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        // 登录、注册接口公开
        if (path.equals("/api/auth/login") || path.equals("/api/auth/register")) {
            return true;
        }
        // GET /api/animals（动物列表）公开
        if ("GET".equalsIgnoreCase(method) && path.equals("/api/animals")) {
            return true;
        }
        // GET /api/animals/{id}（动物详情）公开
        if ("GET".equalsIgnoreCase(method) && path.matches("/api/animals/\\d+")) {
            return true;
        }
        // GET /api/animals/{animalId}/checkins（打卡时间轴）公开
        if ("GET".equalsIgnoreCase(method) && path.matches("/api/animals/\\d+/checkins")) {
            return true;
        }
        return false;
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

        // 获取 Token
        String authHeader = request.getHeader("Authorization");
        boolean hasToken = authHeader != null && authHeader.startsWith("Bearer ");

        // 无 Token 时，检查是否为公开接口
        if (!hasToken) {
            if (isPublicPath(request)) {
                return true;
            }
            sendError(response, 401, "未登录，请先登录");
            return false;
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.validateToken(token)) {
            // Token 无效时，公开接口也放行（但不设置用户上下文）
            if (isPublicPath(request)) {
                return true;
            }
            sendError(response, 401, "登录已过期，请重新登录");
            return false;
        }

        // 解析 Token，设置用户上下文
        Claims claims = jwtUtils.parseToken(token);
        User user = new User();
        user.setId(Long.parseLong(claims.getSubject()));
        user.setUsername(claims.get("username", String.class));
        user.setRole(claims.get("role", Integer.class));
        UserContext.setCurrentUser(user);

        // 检查管理员权限
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequireAdmin requireAdmin = handlerMethod.getMethodAnnotation(RequireAdmin.class);
        if (requireAdmin == null) {
            requireAdmin = handlerMethod.getBeanType().getAnnotation(RequireAdmin.class);
        }
        if (requireAdmin != null && user.getRole() != 1) {
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
}
