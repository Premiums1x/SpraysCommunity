package com.lancer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckInRequest {
    @NotNull(message = "请选择要打卡的动物")
    private Long animalId;

    @NotBlank(message = "打卡内容不能为空")
    private String content;
}
