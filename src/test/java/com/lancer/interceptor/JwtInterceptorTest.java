package com.lancer.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lancer.annotation.RequireAdmin;
import com.lancer.entity.User;
import com.lancer.mapper.UserMapper;
import com.lancer.utils.JwtUtils;
import com.lancer.utils.UserContext;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtInterceptorTest {

    private final JwtUtils jwtUtils = mock(JwtUtils.class);
    private final UserMapper userMapper = mock(UserMapper.class);
    private final JwtInterceptor interceptor = new JwtInterceptor(jwtUtils, userMapper, new ObjectMapper());

    @AfterEach
    void clearContext() {
        UserContext.clear();
    }

    @Test
    void allowsOnlyDocumentedPublicAnimalReadsWithoutToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/animals/12");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, handler("ordinary"))).isTrue();
        verify(userMapper, never()).selectById(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rejectsAnimalWritesWithoutToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/animals");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request, response, handler("adminOnly"))).isFalse();
        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("未登录");
    }

    @Test
    void resolvesAdminRoleFromDatabaseInsteadOfTokenClaim() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("DELETE", "/api/animals/12");
        request.addHeader("Authorization", "Bearer valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("7");
        when(jwtUtils.parseToken("valid-token")).thenReturn(claims);
        User currentUser = new User();
        currentUser.setId(7L);
        currentUser.setRole(0);
        when(userMapper.selectById(7L)).thenReturn(currentUser);

        assertThat(interceptor.preHandle(request, response, handler("adminOnly"))).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    private HandlerMethod handler(String methodName) throws NoSuchMethodException {
        return new HandlerMethod(new SampleController(), SampleController.class.getMethod(methodName));
    }

    static class SampleController {
        public void ordinary() {
        }

        @RequireAdmin
        public void adminOnly() {
        }
    }
}
