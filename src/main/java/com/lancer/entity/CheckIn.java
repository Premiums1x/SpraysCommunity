package com.lancer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("check_in")
public class CheckIn {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long animalId;

    private String content;

    @TableField("is_anonymous")
    private Boolean anonymous;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
