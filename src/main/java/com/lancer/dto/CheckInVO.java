package com.lancer.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CheckInVO {
    private Long id;
    private Long userId;
    private Long animalId;
    private String content;
    private LocalDateTime createTime;
    private Boolean anonymous;
    // 关联的用户信息
    private String username;
    private String userNickname;
    private String userDisplayName;
    private String userAvatar;
    // 关联的动物信息（用于"我的打卡"场景）
    private String animalName;
}
