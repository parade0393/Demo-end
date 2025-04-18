-- RBAC权限后台管理系统数据库初始化脚本
-- 基于MySQL 8.0+

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 创建数据库
-- ----------------------------
CREATE DATABASE IF NOT EXISTS `rbac_system` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `rbac_system`;

-- ----------------------------
-- 用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱（AES加密）',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号（AES加密）',
  `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0禁用 1正常 2锁定）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者用户名',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者用户名',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
  PRIMARY KEY (`id`),
  -- 不是外键,这是唯一索引约束,用于确保用户名不重复
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '角色名称',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1正常 0禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- ----------------------------
-- 角色资源表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_resource`;
CREATE TABLE `sys_role_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `resource_id` varchar(50) NOT NULL COMMENT '资源ID',
  `action` varchar(50) NOT NULL COMMENT '操作权限（view/add/edit/delete）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_resource_action` (`role_id`,`resource_id`,`action`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='角色资源表';

-- ----------------------------
-- 部门表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '部门名称',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父部门ID',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1正常 0禁用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- 用户-角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`,`role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='用户-角色关联表';

-- ----------------------------
-- 用户-部门关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_dept`;
CREATE TABLE `sys_user_dept` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  `is_primary` tinyint NOT NULL DEFAULT 0 COMMENT '是否主部门（0否 1是）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_dept` (`user_id`,`dept_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_dept_id` (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='用户-部门关联表';

-- ----------------------------
-- 菜单表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '菜单名称',
  `path` varchar(100) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(100) DEFAULT NULL COMMENT '组件路径',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父菜单ID',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0禁用 1正常）',
  `type` tinyint NOT NULL COMMENT '类型（1菜单 2按钮）',
  `permission` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ----------------------------
-- 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '字典名称',
  `type` varchar(50) NOT NULL COMMENT '字典类型',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0禁用 1正常）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- ----------------------------
-- 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `dict_type_id` bigint NOT NULL COMMENT '字典类型ID',
  `dict_type` varchar(50) NOT NULL COMMENT '字典类型',
  `label` varchar(50) NOT NULL COMMENT '字典标签',
  `value` varchar(50) NOT NULL COMMENT '字典值',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（0禁用 1正常）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新者',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
  PRIMARY KEY (`id`),
  KEY `idx_dict_type` (`dict_type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- ----------------------------
-- 文件信息表
-- ----------------------------
DROP TABLE IF EXISTS `sys_file`;
CREATE TABLE `sys_file` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_name` varchar(100) NOT NULL COMMENT '文件名',
  `original_name` varchar(100) NOT NULL COMMENT '原始文件名',
  `file_url` varchar(255) NOT NULL COMMENT '文件URL',
  `file_size` bigint NOT NULL COMMENT '文件大小（字节）',
  `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型',
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `upload_by` varchar(50) DEFAULT NULL COMMENT '上传者',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（0否 1是）',
  PRIMARY KEY (`id`),
  KEY `idx_file_type` (`file_type`),
  KEY `idx_upload_time` (`upload_time`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='文件信息表';

-- ----------------------------
-- 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) DEFAULT NULL COMMENT '操作用户',
  `operation` varchar(100) DEFAULT NULL COMMENT '操作内容',
  `method` varchar(10) DEFAULT NULL COMMENT '请求方法',
  `request_url` varchar(255) DEFAULT NULL COMMENT '请求URL',
  `request_method` varchar(100) DEFAULT NULL COMMENT '请求方法',
  `request_params` text COMMENT '请求参数（敏感信息脱敏）',
  `request_ip` varchar(50) DEFAULT NULL COMMENT '请求IP',
  `status` tinyint DEFAULT NULL COMMENT '状态（1成功 0失败）',
  `error_msg` text COMMENT '错误消息',
  `operation_time` datetime DEFAULT NULL COMMENT '操作时间',
  `duration` int DEFAULT NULL COMMENT '执行时长（毫秒）',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_request_url` (`request_url`(191)),
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ----------------------------
-- 登录日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `status` tinyint DEFAULT NULL COMMENT '状态（1成功 0失败）',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址（AES加密）',
  `login_location` varchar(100) DEFAULT NULL COMMENT '登录地点',
  `browser` varchar(50) DEFAULT NULL COMMENT '浏览器',
  `os` varchar(50) DEFAULT NULL COMMENT '操作系统',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  `message` varchar(255) DEFAULT NULL COMMENT '消息',
  PRIMARY KEY (`id`),
  KEY `idx_username` (`username`),
  KEY `idx_login_time` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='登录日志表';

-- ----------------------------
-- 初始化基础数据
-- ----------------------------

-- 初始化用户数据
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', 'admin@example.com', '13800138000', NULL, 1, NOW(), NULL, 'system', NULL, '系统管理员', 0);

-- 初始化角色数据
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, NOW(), NULL, 'system', NULL, '拥有所有权限', 0);
INSERT INTO `sys_role` VALUES (2, '普通用户', 'user', 1, NOW(), NULL, 'system', NULL, '普通用户权限', 0);

-- 初始化部门数据
INSERT INTO `sys_dept` VALUES (1, '总公司', 0, 1, 1, NOW(), NULL, 'system', NULL, 0);
INSERT INTO `sys_dept` VALUES (2, '研发部', 1, 1, 1, NOW(), NULL, 'system', NULL, 0);
INSERT INTO `sys_dept` VALUES (3, '市场部', 1, 2, 1, NOW(), NULL, 'system', NULL, 0);

-- 初始化用户-角色关联数据
INSERT INTO `sys_user_role` VALUES (1, 1, 1);

-- 初始化用户-部门关联数据
INSERT INTO `sys_user_dept` VALUES (1, 1, 1, 1);

-- 初始化菜单数据
INSERT INTO `sys_menu` VALUES (1, 'Dashboard', '/dashboard', 'dashboard/index', 0, 'Odometer', 1, 1, 1, NULL, NOW(), NULL, 'system', NULL, 0);
INSERT INTO `sys_menu` VALUES (2, '系统管理', '/system', 'Layout', 0, 'Setting', 2, 1, 1, NULL, NOW(), NULL, 'system', NULL, 0);
INSERT INTO `sys_menu` VALUES (3, '用户管理', 'user', 'system/user/index', 2, 'User', 1, 1, 1, 'system:user:list', NOW(), NULL, 'system', NULL, 0);
INSERT INTO `sys_menu` VALUES (4, '角色管理', 'role', 'system/role/index', 2, 'UserFilled', 2, 1, 1, 'system:role:list', NOW(), NULL, 'system', NULL, 0);
INSERT INTO `sys_menu` VALUES (5, '菜单管理', 'menu', 'system/menu/index', 2, 'Menu', 3, 1, 1, 'system:menu:list', NOW(), NULL, 'system', NULL, 0);
INSERT INTO `sys_menu` VALUES (6, '部门管理', 'dept', 'system/dept/index', 2, 'OfficeBuilding', 4, 1, 1, 'system:dept:list', NOW(), NULL, 'system', NULL, 0);
INSERT INTO `sys_menu` VALUES (7, '字典管理', 'dict', 'system/dict/index', 2, 'Collection', 5, 1, 1, 'system:dict:list', NOW(), NULL, 'system', NULL, 0);

-- 初始化字典类型数据
INSERT INTO `sys_dict_type` VALUES (1, '用户状态', 'sys_user_status', 1, NOW(), NULL, 'system', NULL, '用户状态列表', 0);
INSERT INTO `sys_dict_type` VALUES (2, '菜单类型', 'sys_menu_type', 1, NOW(), NULL, 'system', NULL, '菜单类型列表', 0);

-- 初始化字典数据
INSERT INTO `sys_dict_data` VALUES (1, 1, 'sys_user_status', '正常', '1', 1, 1, NOW(), NULL, 'system', NULL, '正常状态', 0);
INSERT INTO `sys_dict_data` VALUES (2, 1, 'sys_user_status', '禁用', '0', 2, 1, NOW(), NULL, 'system', NULL, '禁用状态', 0);
INSERT INTO `sys_dict_data` VALUES (3, 1, 'sys_user_status', '锁定', '2', 3, 1, NOW(), NULL, 'system', NULL, '锁定状态', 0);
INSERT INTO `sys_dict_data` VALUES (4, 2, 'sys_menu_type', '菜单', '1', 1, 1, NOW(), NULL, 'system', NULL, '菜单类型', 0);
INSERT INTO `sys_dict_data` VALUES (5, 2, 'sys_menu_type', '按钮', '2', 2, 1, NOW(), NULL, 'system', NULL, '按钮类型', 0);

-- 初始化角色资源权限数据
INSERT INTO `sys_role_resource` VALUES (1, 1, 'user-management', 'view');
INSERT INTO `sys_role_resource` VALUES (2, 1, 'user-management', 'add');
INSERT INTO `sys_role_resource` VALUES (3, 1, 'user-management', 'edit');
INSERT INTO `sys_role_resource` VALUES (4, 1, 'user-management', 'delete');
INSERT INTO `sys_role_resource` VALUES (5, 1, 'role-management', 'view');
INSERT INTO `sys_role_resource` VALUES (6, 1, 'role-management', 'add');
INSERT INTO `sys_role_resource` VALUES (7, 1, 'role-management', 'edit');
INSERT INTO `sys_role_resource` VALUES (8, 1, 'role-management', 'delete');

SET FOREIGN_KEY_CHECKS = 1;