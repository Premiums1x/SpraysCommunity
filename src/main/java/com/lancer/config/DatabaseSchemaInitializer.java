package com.lancer.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSchemaInitializer implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS " +
                        "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'check_in' AND COLUMN_NAME = 'is_anonymous'",
                Integer.class
        );

        if (count != null && count == 0) {
            jdbcTemplate.execute(
                    "ALTER TABLE check_in ADD COLUMN is_anonymous TINYINT NOT NULL DEFAULT 0 " +
                            "COMMENT '是否匿名：0=否, 1=是' AFTER content"
            );
        }
    }
}
