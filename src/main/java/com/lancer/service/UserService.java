package com.lancer.service;

import com.lancer.dto.LoginRequest;
import com.lancer.dto.RegisterRequest;
import com.lancer.entity.User;

import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginRequest request);
    void register(RegisterRequest request);
    User getUserInfo(Long userId);
}
