CREATE DATABASE IF NOT EXISTS `ai-fin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `ai-fin`;

CREATE TABLE IF NOT EXISTS `t_user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    VARCHAR(64)  NOT NULL                COMMENT '用户名',
    `password`    VARCHAR(128) NOT NULL                COMMENT '密码(BCrypt加密)',
    `phone`       VARCHAR(20)  DEFAULT NULL            COMMENT '手机号',
    `email`       VARCHAR(128) DEFAULT NULL            COMMENT '邮箱',
    `nickname`    VARCHAR(64)  DEFAULT NULL            COMMENT '昵称',
    `avatar`      VARCHAR(512) DEFAULT NULL            COMMENT '头像URL',
    `status`      INT          NOT NULL DEFAULT 1      COMMENT '状态: 0=禁用, 1=正常',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_username` (`username`),
    UNIQUE KEY `idx_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
