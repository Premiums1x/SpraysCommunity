package com.lancer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lancer.common.exception.BusinessException;
import com.lancer.dto.LoginRequest;
import com.lancer.dto.RegisterRequest;
import com.lancer.entity.User;
import com.lancer.mapper.UserMapper;
import com.lancer.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private UserServiceImpl userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private String hashedPassword;

    @BeforeEach
    void setUp() {
        hashedPassword = passwordEncoder.encode("123456");
    }

    @Test
    void login_shouldReturnToken() {
        LoginRequest request = new LoginRequest("admin", "123456");
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword(hashedPassword);
        user.setRole(1);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);
        when(jwtUtils.generateToken(1L, "admin", 1)).thenReturn("token123");

        Map<String, Object> result = userService.login(request);

        assertNotNull(result);
        assertEquals("token123", result.get("token"));
        assertNotNull(result.get("user"));
        User returnedUser = (User) result.get("user");
        assertNull(returnedUser.getPassword());
    }

    @Test
    void login_shouldThrowWhenUserNotFound() {
        LoginRequest request = new LoginRequest("nonexistent", "123456");
        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.login(request));
        assertEquals(401, ex.getCode());
        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    void login_shouldThrowWhenPasswordWrong() {
        LoginRequest request = new LoginRequest("admin", "wrongpassword");
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword(hashedPassword);

        when(userMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(user);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.login(request));
        assertEquals(401, ex.getCode());
        assertEquals("用户名或密码错误", ex.getMessage());
    }

    @Test
    void register_shouldSucceed() {
        RegisterRequest request = new RegisterRequest("newuser", "123456", "新用户");
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        userService.register(request);

        verify(userMapper).insert(any(User.class));
    }

    @Test
    void register_shouldThrowWhenUsernameExists() {
        RegisterRequest request = new RegisterRequest("admin", "123456", null);
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.register(request));
        assertEquals(400, ex.getCode());
        assertEquals("用户名已存在", ex.getMessage());
    }

    @Test
    void register_shouldUseUsernameAsDefaultNickname() {
        RegisterRequest request = new RegisterRequest("newuser", "123456", null);
        when(userMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);
        when(userMapper.insert(any(User.class))).thenReturn(1);

        userService.register(request);

        verify(userMapper).insert(any(User.class));
    }

    @Test
    void getUserInfo_shouldReturnUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        user.setPassword(hashedPassword);
        when(userMapper.selectById(1L)).thenReturn(user);

        User result = userService.getUserInfo(1L);

        assertNotNull(result);
        assertNull(result.getPassword());
        assertEquals("admin", result.getUsername());
    }

    @Test
    void getUserInfo_shouldThrowWhenNotFound() {
        when(userMapper.selectById(999L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class, () -> userService.getUserInfo(999L));
        assertEquals(404, ex.getCode());
        assertEquals("用户不存在", ex.getMessage());
    }
}
