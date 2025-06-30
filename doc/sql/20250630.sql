/*
 Navicat Premium Data Transfer

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 80037 (8.0.37)
 Source Host           : localhost:3306
 Source Schema         : rbac_admin

 Target Server Type    : MySQL
 Target Server Version : 80037 (8.0.37)
 File Encoding         : 65001

 Date: 30/06/2025 09:46:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for file_info
-- ----------------------------
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info`  (
  `id` bigint NOT NULL COMMENT '文件ID',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
  `stored_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '存储文件名(UUID)',
  `file_path` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件存储路径',
  `file_size` bigint NOT NULL COMMENT '文件大小(字节)',
  `mime_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'MIME类型',
  `file_hash` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '文件哈希值(MD5/SHA256)',
  `is_image` tinyint(1) NULL DEFAULT 0 COMMENT '是否为图片(0:否, 1:是)',
  `compressed` tinyint(1) NULL DEFAULT 0 COMMENT '是否已压缩(0:否, 1:是)',
  `reference_count` int NULL DEFAULT 1 COMMENT '引用计数(用于去重)',
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `uploader_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '上传者IP',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户代理',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除(0:未删除, 1:已删除)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_hash`(`file_hash` ASC) USING BTREE,
  INDEX `idx_mime_type`(`mime_type` ASC) USING BTREE,
  INDEX `idx_upload_time`(`upload_time` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file_info
-- ----------------------------
INSERT INTO `file_info` VALUES (1935179556219002881, '1.jpg', '4117a01e764146b2af21de7bc486e063.jpg', '4/1/4117a01e764146b2af21de7bc486e063.jpg', 43425, 'image/jpeg', 'f32d48625da5953ac993c6119b05578d', 1, 1, 2, '2025-06-18 11:35:35', '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36', 0, '2025-06-18 11:35:35', '2025-06-18 11:45:09');

-- ----------------------------
-- Table structure for file_reference
-- ----------------------------
DROP TABLE IF EXISTS `file_reference`;
CREATE TABLE `file_reference`  (
  `id` bigint NOT NULL COMMENT '引用ID',
  `file_id` bigint NOT NULL COMMENT '文件ID',
  `reference_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '引用名称(用户看到的文件名)',
  `reference_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'upload' COMMENT '引用类型(upload:上传, copy:复制等)',
  `uploader_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '上传者信息',
  `upload_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '引用创建时间',
  `deleted` tinyint(1) NULL DEFAULT 0 COMMENT '逻辑删除(0:未删除, 1:已删除)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_file_id`(`file_id` ASC) USING BTREE,
  INDEX `idx_upload_time`(`upload_time` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE,
  CONSTRAINT `file_reference_ibfk_1` FOREIGN KEY (`file_id`) REFERENCES `file_info` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '文件引用表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of file_reference
-- ----------------------------
INSERT INTO `file_reference` VALUES (1935179556281917441, 1935179556219002881, '1.jpg', 'upload', '0:0:0:0:0:0:0:1', '2025-06-18 11:35:35', 0, '2025-06-18 11:35:35', '2025-06-18 11:35:35');
INSERT INTO `file_reference` VALUES (1935181968220684290, 1935179556219002881, '1.jpg', 'upload', '0:0:0:0:0:0:0:1', '2025-06-18 11:45:10', 0, '2025-06-18 11:45:10', '2025-06-18 11:45:10');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父部门ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '部门名称',
  `leader` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-停用）',
  `sort` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (1, 0, '总公司', '张三', '13800138000', 'admin@example.com', 1, 1, '2025-04-21 10:38:42', NULL, 0);
INSERT INTO `sys_dept` VALUES (2, 1, '研发部', '李四', '13800138001', 'dev@example.com', 1, 1, '2025-04-21 10:38:42', NULL, 0);
INSERT INTO `sys_dept` VALUES (3, 1, '市场部', '王五', '13800138002', 'market@example.com', 1, 2, '2025-04-21 10:38:42', NULL, 0);
INSERT INTO `sys_dept` VALUES (4, 1, '财务部', '赵七', '13800138003', 'finance@example.com', 1, 3, '2025-04-21 10:38:42', '2025-05-08 15:11:00', 0);
INSERT INTO `sys_dept` VALUES (5, 2, '前端组', '钱七', '13800138004', 'frontend@example.com', 1, 1, '2025-04-21 10:38:42', NULL, 0);
INSERT INTO `sys_dept` VALUES (6, 2, '后端组', '孙八', '13800138005', 'backend@example.com', 1, 2, '2025-04-21 10:38:42', NULL, 0);
INSERT INTO `sys_dept` VALUES (7, 0, '上海分公司', '武', '13451234567', '12@12.com', 1, 0, '2025-04-21 10:38:42', '2025-05-08 15:36:53', 1);
INSERT INTO `sys_dept` VALUES (8, 4, '会计', '杨', '13001239834', '120@11.com', 1, 0, '2025-05-08 15:30:34', '2025-05-08 15:30:34', 0);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父节点ID',
  `tree_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父节点路径',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '菜单名称',
  `type` tinyint NOT NULL COMMENT '菜单类型（1-目录 2-菜单 3-按钮 4-外链）',
  `route_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由名称（Vue Router 命名路由名称）',
  `route_path` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由路径（Vue Router 中定义的 URL 路径）',
  `component` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '组件路径（相对于src/views/，或者后缀.vue）',
  `perm` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '[按钮] 权限标识',
  `always_show` tinyint NULL DEFAULT NULL COMMENT '[目录] 是否一个路由显示（1-是 0-否）',
  `keep_alive` tinyint NULL DEFAULT NULL COMMENT '[菜单] 是否开启页面缓存（1-是 0-否）',
  `visible` tinyint NULL DEFAULT 1 COMMENT '显示状态（1-显示 0-隐藏）',
  `sort` int NULL DEFAULT 0 COMMENT '排序',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '菜单图标',
  `redirect` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跳转路径',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由参数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '0', '系统管理', 1, 'System', '/system', 'Layout', NULL, 1, 0, 1, 2, 'Setting', '/system/user', '2025-04-21 10:38:43', '2025-05-07 11:20:47', NULL);
INSERT INTO `sys_menu` VALUES (2, 1, '0,1', '用户管理', 2, 'SystemUser', 'user', 'system/user/index', NULL, 1, 0, 1, 1, 'User', NULL, '2025-04-21 10:38:43', '2025-05-29 11:12:12', NULL);
INSERT INTO `sys_menu` VALUES (3, 2, '0,1,2', '用户查询', 3, NULL, NULL, NULL, 'system:user:query', NULL, NULL, 1, 1, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:43', NULL);
INSERT INTO `sys_menu` VALUES (4, 2, '0,1,2', '用户新增', 3, NULL, NULL, NULL, 'system:user:add', NULL, NULL, 1, 2, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:44', NULL);
INSERT INTO `sys_menu` VALUES (5, 2, '0,1,2', '用户编辑', 3, NULL, NULL, NULL, 'system:user:edit', NULL, NULL, 1, 3, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:46', NULL);
INSERT INTO `sys_menu` VALUES (6, 2, '0,1,2', '用户删除', 3, NULL, NULL, NULL, 'system:user:delete', NULL, NULL, 1, 4, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:50', NULL);
INSERT INTO `sys_menu` VALUES (7, 2, '0,1,2', '重置密码', 3, NULL, NULL, NULL, 'system:user:resetPwd', NULL, NULL, 1, 5, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:51', NULL);
INSERT INTO `sys_menu` VALUES (8, 2, '0,1,2', '用户导入', 3, NULL, NULL, NULL, 'system:user:import', NULL, NULL, 1, 6, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:52', NULL);
INSERT INTO `sys_menu` VALUES (9, 2, '0,1,2', '用户导出', 3, NULL, NULL, NULL, 'system:user:export', NULL, NULL, 1, 7, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:56', NULL);
INSERT INTO `sys_menu` VALUES (10, 1, '0,1', '角色管理', 2, 'SystemRole', 'role', 'system/role/index', NULL, 1, 0, 1, 2, 'UserFilled', NULL, '2025-04-21 10:38:43', '2025-05-29 11:12:27', NULL);
INSERT INTO `sys_menu` VALUES (11, 10, '0,1,10', '角色查询', 3, NULL, NULL, NULL, 'system:role:query', NULL, NULL, 1, 1, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:10', NULL);
INSERT INTO `sys_menu` VALUES (12, 10, '0,1,2', '角色新增', 3, NULL, NULL, NULL, 'system:role:add', NULL, NULL, 1, 2, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:13', NULL);
INSERT INTO `sys_menu` VALUES (13, 10, '0,1,2', '角色编辑', 3, NULL, NULL, NULL, 'system:role:edit', NULL, NULL, 1, 3, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:14', NULL);
INSERT INTO `sys_menu` VALUES (14, 10, '0,1,2', '角色删除', 3, NULL, NULL, NULL, 'system:role:delete', NULL, NULL, 1, 4, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:15', NULL);
INSERT INTO `sys_menu` VALUES (15, 1, '0,1', '菜单管理', 2, 'SystemMenu', 'menu', 'system/menu/index', NULL, 1, 0, 1, 3, 'Menu', NULL, '2025-04-21 10:38:43', '2025-05-29 11:12:35', NULL);
INSERT INTO `sys_menu` VALUES (16, 15, '0,1,15', '菜单查询', 3, NULL, NULL, NULL, 'system:menu:query', NULL, NULL, 1, 1, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:22', NULL);
INSERT INTO `sys_menu` VALUES (17, 15, '0,1,15', '菜单新增', 3, NULL, NULL, NULL, 'system:menu:add', NULL, NULL, 1, 2, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:28', NULL);
INSERT INTO `sys_menu` VALUES (18, 15, '0,1,15', '菜单编辑', 3, NULL, NULL, NULL, 'system:menu:edit', NULL, NULL, 1, 3, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:29', NULL);
INSERT INTO `sys_menu` VALUES (19, 15, '0,1,15', '菜单删除', 3, NULL, NULL, NULL, 'system:menu:delete', NULL, NULL, 1, 4, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:31', NULL);
INSERT INTO `sys_menu` VALUES (20, 1, '0,1', '部门管理', 2, 'SystemDept', 'dept', 'system/dept/index', NULL, 1, 0, 1, 4, 'OfficeBuilding', NULL, '2025-04-21 10:38:43', '2025-05-29 11:12:44', NULL);
INSERT INTO `sys_menu` VALUES (21, 20, '0,1,20', '部门新增', 3, NULL, NULL, NULL, 'system:dept:add', NULL, NULL, 1, 1, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:28', NULL);
INSERT INTO `sys_menu` VALUES (22, 20, '0,1,20', '部门编辑', 3, NULL, NULL, NULL, 'system:dept:edit', NULL, NULL, 1, 2, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:28', NULL);
INSERT INTO `sys_menu` VALUES (23, 20, '0,1,20', '部门删除', 3, NULL, NULL, NULL, 'system:dept:delete', NULL, NULL, 1, 3, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:05:14', NULL);
INSERT INTO `sys_menu` VALUES (25, 20, '0,1,20', '部门查询', 3, NULL, NULL, NULL, 'system:dept:query', 0, 0, 1, 4, NULL, NULL, '2025-05-08 09:52:12', '2025-05-08 09:52:12', NULL);
INSERT INTO `sys_menu` VALUES (30, 0, '0', '内容管理', 1, NULL, '/content', 'Layout', NULL, 0, 0, 1, 3, 'Document', '/content/article', '2025-05-23 14:27:04', '2025-05-23 14:37:20', NULL);
INSERT INTO `sys_menu` VALUES (31, 30, '0,30', '文章管理', 2, 'ContentArticle', 'article', 'content/article/index', NULL, 0, 1, 1, 0, 'Tickets', NULL, '2025-05-23 14:29:31', '2025-06-06 15:49:15', NULL);
INSERT INTO `sys_menu` VALUES (32, 30, '0,30', '分类管理', 2, 'ContentCategory', 'category', 'content/category/index', NULL, 0, 0, 1, 0, 'Files', NULL, '2025-05-23 14:38:36', '2025-05-29 11:13:10', NULL);
INSERT INTO `sys_menu` VALUES (33, 0, '0', '外链文档', 4, NULL, 'https://www.baidu.com/', NULL, NULL, 0, 0, 1, 4, 'Link', NULL, '2025-05-23 15:26:57', '2025-05-23 15:27:42', NULL);
INSERT INTO `sys_menu` VALUES (34, 0, '0', '路由参数', 1, NULL, '/route-param', 'Layout', NULL, 1, 0, 1, 3, 'Cpu', '/route-param/con/route-param-type1?type=1', '2025-06-06 16:00:09', '2025-06-13 16:47:37', NULL);
INSERT INTO `sys_menu` VALUES (35, 37, '0,34,37', '参数(type=1)', 2, 'RouteParamsDemo', 'route-param-type1', 'demo/route-params', NULL, 0, 0, 1, 0, 'Coordinate', NULL, '2025-06-06 16:02:03', '2025-06-13 16:07:18', '[{\"key\":\"type\",\"value\":\"1\"}]');
INSERT INTO `sys_menu` VALUES (36, 37, '0,34,37', '参数(type=2)', 2, 'RoutParamType2', 'route-param-type2', 'demo/route-params', NULL, 0, 0, 1, 1, 'Drizzling', NULL, '2025-06-06 16:04:55', '2025-06-13 16:07:23', '[{\"key\":\"type\",\"value\":\"2\"}]');
INSERT INTO `sys_menu` VALUES (37, 34, '0,34', '嵌套菜单', 1, NULL, 'con', 'EmptyParent', NULL, 0, 0, 1, 0, 'Brush', '/route-param/con/route-param-type1?type=1', '2025-06-13 16:04:44', '2025-06-13 16:47:44', NULL);

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `module` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作模块',
  `operation` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作类型',
  `method` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '方法名称',
  `request_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求URL',
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '请求方式',
  `request_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数',
  `response_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '返回结果',
  `error_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误消息',
  `operation_time` int NOT NULL COMMENT '操作耗时（毫秒）',
  `user_id` bigint NOT NULL COMMENT '操作人ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作人用户名',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'IP地址',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '操作状态（1-成功，0-失败）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '操作日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-停用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `sort` int NOT NULL DEFAULT 0 COMMENT '显示顺序',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '系统内置超级管理员角色', 1, NULL, '2025-04-21 10:38:43', NULL, '2025-04-27 17:31:04', 0);
INSERT INTO `sys_role` VALUES (2, '普通用户', 'user', 1, '普通用户角色', 2, NULL, '2025-04-21 10:38:43', NULL, '2025-04-27 17:31:09', 0);
INSERT INTO `sys_role` VALUES (3, '开发人员', 'dev', 1, '开发人员角色', 3, NULL, '2025-04-21 10:38:43', NULL, '2025-05-07 17:46:46', 1);
INSERT INTO `sys_role` VALUES (4, '测试1', 'test1', 1, '这是测试 1', 3, NULL, '2025-05-07 17:48:55', NULL, '2025-05-07 17:48:55', 0);

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 2);
INSERT INTO `sys_role_menu` VALUES (2, 3);
INSERT INTO `sys_role_menu` VALUES (2, 10);
INSERT INTO `sys_role_menu` VALUES (2, 11);
INSERT INTO `sys_role_menu` VALUES (2, 15);
INSERT INTO `sys_role_menu` VALUES (2, 16);
INSERT INTO `sys_role_menu` VALUES (4, 11);
INSERT INTO `sys_role_menu` VALUES (4, 1);
INSERT INTO `sys_role_menu` VALUES (4, 10);
INSERT INTO `sys_role_menu` VALUES (1, 1);
INSERT INTO `sys_role_menu` VALUES (1, 2);
INSERT INTO `sys_role_menu` VALUES (1, 3);
INSERT INTO `sys_role_menu` VALUES (1, 4);
INSERT INTO `sys_role_menu` VALUES (1, 5);
INSERT INTO `sys_role_menu` VALUES (1, 6);
INSERT INTO `sys_role_menu` VALUES (1, 7);
INSERT INTO `sys_role_menu` VALUES (1, 8);
INSERT INTO `sys_role_menu` VALUES (1, 9);
INSERT INTO `sys_role_menu` VALUES (1, 10);
INSERT INTO `sys_role_menu` VALUES (1, 11);
INSERT INTO `sys_role_menu` VALUES (1, 12);
INSERT INTO `sys_role_menu` VALUES (1, 13);
INSERT INTO `sys_role_menu` VALUES (1, 14);
INSERT INTO `sys_role_menu` VALUES (1, 15);
INSERT INTO `sys_role_menu` VALUES (1, 16);
INSERT INTO `sys_role_menu` VALUES (1, 17);
INSERT INTO `sys_role_menu` VALUES (1, 18);
INSERT INTO `sys_role_menu` VALUES (1, 19);
INSERT INTO `sys_role_menu` VALUES (1, 20);
INSERT INTO `sys_role_menu` VALUES (1, 21);
INSERT INTO `sys_role_menu` VALUES (1, 22);
INSERT INTO `sys_role_menu` VALUES (1, 23);
INSERT INTO `sys_role_menu` VALUES (1, 25);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码（加密存储）',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-禁用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者ID',
  `update_time` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `is_deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除（1-是，0-否）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'admin', '$2a$10$WxBITjBACIGLk8kBs9ZlPO/N9Wo4EyFuSLm/dW8U5qEceBYe/lIIq', '岁月', '13034542345', '162@163.com', 1, NULL, 1, '财务经理', NULL, '2025-04-21 10:38:43', NULL, '2025-05-08 16:02:37', NULL, 0);
INSERT INTO `sys_user` VALUES (2, 'user', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '普通用户', '13800138001', 'user@example.com', 2, NULL, 1, NULL, NULL, '2025-04-21 10:38:43', NULL, NULL, NULL, 0);
INSERT INTO `sys_user` VALUES (3, 'dev', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '开发人员', '13800138002', 'dev@example.com', 6, NULL, 1, NULL, NULL, '2025-04-21 10:38:43', NULL, NULL, NULL, 0);
INSERT INTO `sys_user` VALUES (4, 'henry', '$2a$10$iulAi15l/S0w1hH6QDCzqO.22IFrYYkrVbPMYyqI783YT8nd9Z6mC', '杰瑞', '13012345675', '132@163.com', 3, NULL, 1, '新增市场部用户', NULL, '2025-05-08 14:07:56', NULL, '2025-05-08 14:32:02', NULL, 0);
INSERT INTO `sys_user` VALUES (5, 'zhang', '$2a$10$0mrF2oXWPeiBTq2CsySON.UoDsjzZGpDwWsJB9c66BnTXeeDxBQwa', '张', '13802340987', '123@123.com', 8, NULL, 1, '会计', NULL, '2025-05-08 15:31:48', NULL, '2025-05-08 17:05:25', NULL, 1);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID'
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (4, 4);
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (1, 2);
INSERT INTO `sys_user_role` VALUES (1, 4);

-- ----------------------------
-- Table structure for system_config
-- ----------------------------
DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config`  (
  `id` bigint NOT NULL COMMENT '配置ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '配置值',
  `config_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '配置描述',
  `config_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT 'STRING' COMMENT '配置类型(STRING, NUMBER, BOOLEAN, JSON)',
  `is_system` tinyint(1) NULL DEFAULT 0 COMMENT '是否为系统配置(0:否, 1:是)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `config_key`(`config_key` ASC) USING BTREE,
  INDEX `idx_config_key`(`config_key` ASC) USING BTREE,
  INDEX `idx_is_system`(`is_system` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_config
-- ----------------------------
INSERT INTO `system_config` VALUES (1, 'max_file_size', '104857600', '最大文件大小(字节)', 'NUMBER', 1, '2025-06-17 15:26:04', '2025-06-17 15:26:04');
INSERT INTO `system_config` VALUES (2, 'allowed_mime_types', '[\"image/jpeg\",\"image/png\",\"image/gif\",\"image/webp\",\"image/bmp\",\"application/pdf\",\"text/plain\",\"application/msword\",\"application/vnd.openxmlformats-officedocument.wordprocessingml.document\",\"application/vnd.ms-excel\",\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\",\"application/zip\",\"application/x-rar-compressed\"]', '允许的MIME类型', 'JSON', 1, '2025-06-17 15:26:04', '2025-06-17 15:26:04');
INSERT INTO `system_config` VALUES (3, 'image_compress_enabled', 'true', '是否启用图片压缩', 'BOOLEAN', 1, '2025-06-17 15:26:04', '2025-06-17 15:26:04');
INSERT INTO `system_config` VALUES (4, 'image_compress_quality', '0.8', '图片压缩质量', 'NUMBER', 1, '2025-06-17 15:26:04', '2025-06-17 15:26:04');
INSERT INTO `system_config` VALUES (5, 'image_max_width', '1920', '图片最大宽度', 'NUMBER', 1, '2025-06-17 15:26:04', '2025-06-17 15:26:04');
INSERT INTO `system_config` VALUES (6, 'image_max_height', '1080', '图片最大高度', 'NUMBER', 1, '2025-06-17 15:26:04', '2025-06-17 15:26:04');
INSERT INTO `system_config` VALUES (7, 'deduplication_enabled', 'true', '是否启用文件去重', 'BOOLEAN', 1, '2025-06-17 15:26:04', '2025-06-17 15:26:04');
INSERT INTO `system_config` VALUES (8, 'hash_algorithm', 'MD5', '哈希算法(MD5/SHA256)', 'STRING', 1, '2025-06-17 15:26:04', '2025-06-17 15:26:04');

-- ----------------------------
-- Table structure for upload_log
-- ----------------------------
DROP TABLE IF EXISTS `upload_log`;
CREATE TABLE `upload_log`  (
  `id` bigint NOT NULL COMMENT '日志ID',
  `file_id` bigint NULL DEFAULT NULL COMMENT '文件ID(上传成功时关联)',
  `original_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '原始文件名',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小(字节)',
  `mime_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'MIME类型',
  `upload_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '上传状态(SUCCESS:成功, FAILED:失败, PROCESSING:处理中)',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '错误信息',
  `uploader_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '上传者IP',
  `user_agent` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '用户代理',
  `upload_start_time` datetime NOT NULL COMMENT '上传开始时间',
  `upload_end_time` datetime NULL DEFAULT NULL COMMENT '上传结束时间',
  `processing_time_ms` bigint NULL DEFAULT NULL COMMENT '处理耗时(毫秒)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `file_id`(`file_id` ASC) USING BTREE,
  INDEX `idx_upload_status`(`upload_status` ASC) USING BTREE,
  INDEX `idx_upload_start_time`(`upload_start_time` ASC) USING BTREE,
  INDEX `idx_uploader_ip`(`uploader_ip` ASC) USING BTREE,
  CONSTRAINT `upload_log_ibfk_1` FOREIGN KEY (`file_id`) REFERENCES `file_info` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '上传日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of upload_log
-- ----------------------------
INSERT INTO `upload_log` VALUES (1935179551882092546, 1935179556219002881, '1.jpg', 343478, 'image/jpeg', 'SUCCESS', NULL, '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36', '2025-06-18 11:35:34', '2025-06-18 11:35:35', 1108, '2025-06-18 11:35:34');
INSERT INTO `upload_log` VALUES (1935181967117582337, 1935179556219002881, '1.jpg', 343478, 'image/jpeg', 'SUCCESS', NULL, '0:0:0:0:0:0:0:1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/137.0.0.0 Safari/537.36', '2025-06-18 11:45:10', '2025-06-18 11:45:10', 291, '2025-06-18 11:45:10');

-- ----------------------------
-- Procedure structure for CleanupUnreferencedFiles
-- ----------------------------
DROP PROCEDURE IF EXISTS `CleanupUnreferencedFiles`;
delimiter ;;
CREATE PROCEDURE `CleanupUnreferencedFiles`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE file_id_var BIGINT;
    DECLARE ref_count INT;
    
    -- 声明游标
    DECLARE file_cursor CURSOR FOR 
        SELECT id FROM file_info WHERE deleted = 0;
    
    -- 声明异常处理
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 开启游标
    OPEN file_cursor;
    
    read_loop: LOOP
        FETCH file_cursor INTO file_id_var;
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 检查引用计数
        SELECT COUNT(*) INTO ref_count 
        FROM file_reference 
        WHERE file_id = file_id_var AND deleted = 0;
        
        -- 如果没有引用，标记为删除
        IF ref_count = 0 THEN
            UPDATE file_info SET deleted = 1 WHERE id = file_id_var;
        ELSE
            -- 更新引用计数
            UPDATE file_info SET reference_count = ref_count WHERE id = file_id_var;
        END IF;
    END LOOP;
    
    -- 关闭游标
    CLOSE file_cursor;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
