package com.lancer.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("animal")
public class Animal {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Integer type;

    private String area;

    private String coverImage;

    private String description;

    private String aliases;

    private Integer gender;

    private String personalityTags;

    private Boolean sterilized;

    private String healthStatus;

    private LocalDate firstSeenDate;

    private String activeTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
