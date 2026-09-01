ALTER TABLE `animal`
    ADD COLUMN `aliases` VARCHAR(255) DEFAULT NULL COMMENT '常用别名，逗号分隔' AFTER `description`,
    ADD COLUMN `gender` TINYINT NOT NULL DEFAULT 0 COMMENT '性别：0=未知, 1=公, 2=母' AFTER `aliases`,
    ADD COLUMN `personality_tags` VARCHAR(255) DEFAULT NULL COMMENT '性格标签，逗号分隔' AFTER `gender`,
    ADD COLUMN `sterilized` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已绝育：0=否或未知, 1=是' AFTER `personality_tags`,
    ADD COLUMN `health_status` VARCHAR(20) NOT NULL DEFAULT 'HEALTHY' COMMENT '健康状态' AFTER `sterilized`,
    ADD COLUMN `first_seen_date` DATE DEFAULT NULL COMMENT '首次发现日期' AFTER `health_status`,
    ADD COLUMN `active_time` VARCHAR(100) DEFAULT NULL COMMENT '常见活跃时段' AFTER `first_seen_date`,
    ADD CONSTRAINT `chk_animal_gender` CHECK (`gender` IN (0, 1, 2)),
    ADD CONSTRAINT `chk_animal_sterilized` CHECK (`sterilized` IN (0, 1)),
    ADD CONSTRAINT `chk_animal_health` CHECK (`health_status` IN ('HEALTHY', 'OBSERVE', 'NEEDS_HELP'));
