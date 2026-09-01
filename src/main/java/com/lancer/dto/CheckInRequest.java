package com.lancer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckInRequest {
    @NotNull(message = "请选择要打卡的动物")
    @Positive(message = "动物编号不正确")
    private Long animalId;

    @NotBlank(message = "打卡内容不能为空")
    @Size(max = 500, message = "打卡内容不能超过500个字符")
    private String content;

    private Boolean anonymous = false;
}
