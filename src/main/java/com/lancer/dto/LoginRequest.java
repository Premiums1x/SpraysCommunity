package com.lancer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(max = 20, message = "用户名不能超过20个字符")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(max = 72, message = "密码不能超过72个字符")
    private String password;
}
