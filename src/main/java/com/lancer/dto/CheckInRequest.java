package com.lancer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckInRequest(
        @NotNull(message = "请选择要打卡的动物")
        Long animalId,

        @NotBlank(message = "打卡内容不能为空")
        String content,

        Boolean anonymous
) {
    public CheckInRequest {
        if (anonymous == null) {
            anonymous = false;
        }
    }
}
