package com.lancer.controller;

import com.lancer.common.result.Result;
import com.lancer.dto.LoginRequest;
import com.lancer.dto.RegisterRequest;
import com.lancer.entity.User;
import com.lancer.service.UserService;
import com.lancer.utils.UserContext;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> data = userService.login(request);
        return Result.success("登录成功", data);
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return Result.success("注册成功", null);
    }

    @GetMapping("/info")
    public Result<User> getUserInfo() {
        Long userId = UserContext.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        return Result.success(user);
    }
}
