package com.lancer.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AnimalCreateRequest {
    @NotBlank(message = "名字不能为空")
    @Size(max = 50, message = "名字不能超过50个字符")
    private String name;

    @NotNull(message = "请选择动物类型")
    @Min(value = 1, message = "动物类型不正确")
    @Max(value = 2, message = "动物类型不正确")
    private Integer type;

    @NotBlank(message = "常驻区域不能为空")
    @Size(max = 100, message = "常驻区域不能超过100个字符")
    private String area;

    @Size(max = 1000, message = "简介不能超过1000个字符")
    private String description;

    @Size(max = 255, message = "别名不能超过255个字符")
    private String aliases;

    @Min(value = 0, message = "性别选项不正确")
    @Max(value = 2, message = "性别选项不正确")
    private Integer gender = 0;

    @Size(max = 255, message = "性格标签不能超过255个字符")
    private String personalityTags;

    private Boolean sterilized = false;

    @Pattern(regexp = "HEALTHY|OBSERVE|NEEDS_HELP", message = "健康状态不正确")
    private String healthStatus = "HEALTHY";

    @PastOrPresent(message = "首次发现日期不能晚于今天")
    private LocalDate firstSeenDate;

    @Size(max = 100, message = "活跃时段不能超过100个字符")
    private String activeTime;
}
