package com.lancer.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lancer.entity.User;
import com.lancer.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UserMapper userMapper;
    private final String username;
    private final String password;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminBootstrap(
            UserMapper userMapper,
            @Value("${app.bootstrap-admin.username:}") String username,
            @Value("${app.bootstrap-admin.password:}") String password) {
        this.userMapper = userMapper;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(username) && !StringUtils.hasText(password)) {
            return;
        }
        if (!StringUtils.hasText(username) || password.length() < 8) {
            throw new IllegalStateException("初始化管理员需要同时设置用户名和至少8位密码");
        }
        if (username.trim().length() > 20) {
            throw new IllegalStateException("初始化管理员用户名不能超过20个字符");
        }

        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, username.trim())
        );
        if (count > 0) {
            log.info("Bootstrap administrator already exists, skipping creation");
            return;
        }

        User administrator = new User();
        administrator.setUsername(username.trim());
        administrator.setNickname("系统管理员");
        administrator.setPassword(passwordEncoder.encode(password));
        administrator.setRole(1);
        userMapper.insert(administrator);
        log.info("Bootstrap administrator created; clear bootstrap credentials before the next launch");
    }
}
