package com.lancer.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AnimalQueryRequest {
    @Size(max = 50, message = "名字关键词不能超过50个字符")
    private String name;      // 模糊查询名字

    @Min(value = 1, message = "动物类型不正确")
    @Max(value = 2, message = "动物类型不正确")
    private Integer type;     // 类型筛选：1=猫, 2=狗, null=全部

    @Min(value = 1, message = "页码必须大于0")
    private Integer page = 1;

    @Min(value = 1, message = "每页数量必须大于0")
    @Max(value = 50, message = "每页最多查询50条")
    private Integer size = 10;
}
