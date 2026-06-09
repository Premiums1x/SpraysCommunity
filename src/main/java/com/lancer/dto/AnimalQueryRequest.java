package com.lancer.dto;

import lombok.Data;

@Data
public class AnimalQueryRequest {
    private String name;      // 模糊查询名字
    private Integer type;     // 类型筛选：1=猫, 2=狗, null=全部
    private Integer page = 1;
    private Integer size = 10;
}
