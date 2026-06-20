-- 创建数据库
CREATE DATABASE IF NOT EXISTS strays_community DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE strays_community;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `role` TINYINT NOT NULL DEFAULT 0 COMMENT '角色：0=普通用户, 1=管理员',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像路径',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 动物档案表
CREATE TABLE IF NOT EXISTS `animal` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '动物ID',
    `name` VARCHAR(50) NOT NULL COMMENT '暂定名字',
    `type` TINYINT NOT NULL COMMENT '类型：1=猫, 2=狗',
    `area` VARCHAR(100) NOT NULL COMMENT '常驻区域',
    `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面照片路径',
    `description` TEXT DEFAULT NULL COMMENT '简介描述',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除, 1=已删除',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动物档案表';

-- 打卡动态表
CREATE TABLE IF NOT EXISTS `check_in` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '打卡ID',
    `user_id` BIGINT NOT NULL COMMENT '打卡用户ID',
    `animal_id` BIGINT NOT NULL COMMENT '关联动物ID',
    `content` TEXT NOT NULL COMMENT '打卡文字描述',
    `is_anonymous` TINYINT NOT NULL DEFAULT 0 COMMENT '是否匿名：0=否, 1=是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '打卡时间',
    PRIMARY KEY (`id`),
    KEY `idx_animal_id_create_time` (`animal_id`, `create_time` DESC),
    KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打卡动态表';

-- 插入默认管理员账号（密码为 admin123，BCrypt加密）
INSERT INTO `user` (`username`, `password`, `nickname`, `role`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 1);
