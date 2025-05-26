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

 Date: 26/05/2025 14:07:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

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
  `params` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '路由参数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '菜单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, 0, '0', '系统管理', 1, 'System', '/system', 'Layout', NULL, 1, 0, 1, 2, 'Setting', '/system/user', '2025-04-21 10:38:43', '2025-05-07 11:20:47', NULL);
INSERT INTO `sys_menu` VALUES (2, 1, '0,1', '用户管理', 2, 'User', 'user', 'system/user/index', NULL, 1, 0, 1, 1, 'User', NULL, '2025-04-21 10:38:43', '2025-05-23 09:33:29', NULL);
INSERT INTO `sys_menu` VALUES (3, 2, '0,1,2', '用户查询', 3, NULL, NULL, NULL, 'system:user:query', NULL, NULL, 1, 1, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:43', NULL);
INSERT INTO `sys_menu` VALUES (4, 2, '0,1,2', '用户新增', 3, NULL, NULL, NULL, 'system:user:add', NULL, NULL, 1, 2, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:44', NULL);
INSERT INTO `sys_menu` VALUES (5, 2, '0,1,2', '用户编辑', 3, NULL, NULL, NULL, 'system:user:edit', NULL, NULL, 1, 3, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:46', NULL);
INSERT INTO `sys_menu` VALUES (6, 2, '0,1,2', '用户删除', 3, NULL, NULL, NULL, 'system:user:delete', NULL, NULL, 1, 4, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:50', NULL);
INSERT INTO `sys_menu` VALUES (7, 2, '0,1,2', '重置密码', 3, NULL, NULL, NULL, 'system:user:resetPwd', NULL, NULL, 1, 5, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:51', NULL);
INSERT INTO `sys_menu` VALUES (8, 2, '0,1,2', '用户导入', 3, NULL, NULL, NULL, 'system:user:import', NULL, NULL, 1, 6, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:52', NULL);
INSERT INTO `sys_menu` VALUES (9, 2, '0,1,2', '用户导出', 3, NULL, NULL, NULL, 'system:user:export', NULL, NULL, 1, 7, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:00:56', NULL);
INSERT INTO `sys_menu` VALUES (10, 1, '0,1', '角色管理', 2, 'Role', 'role', 'system/role/index', NULL, 1, 0, 1, 2, 'UserFilled', NULL, '2025-04-21 10:38:43', '2025-05-23 09:33:34', NULL);
INSERT INTO `sys_menu` VALUES (11, 10, '0,1,10', '角色查询', 3, NULL, NULL, NULL, 'system:role:query', NULL, NULL, 1, 1, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:10', NULL);
INSERT INTO `sys_menu` VALUES (12, 10, '0,1,2', '角色新增', 3, NULL, NULL, NULL, 'system:role:add', NULL, NULL, 1, 2, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:13', NULL);
INSERT INTO `sys_menu` VALUES (13, 10, '0,1,2', '角色编辑', 3, NULL, NULL, NULL, 'system:role:edit', NULL, NULL, 1, 3, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:14', NULL);
INSERT INTO `sys_menu` VALUES (14, 10, '0,1,2', '角色删除', 3, NULL, NULL, NULL, 'system:role:delete', NULL, NULL, 1, 4, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:15', NULL);
INSERT INTO `sys_menu` VALUES (15, 1, '0,1', '菜单管理', 2, 'Menu', 'menu', 'system/menu/index', NULL, 1, 0, 1, 3, 'Menu', NULL, '2025-04-21 10:38:43', '2025-05-23 09:33:39', NULL);
INSERT INTO `sys_menu` VALUES (16, 15, '0,1,15', '菜单查询', 3, NULL, NULL, NULL, 'system:menu:query', NULL, NULL, 1, 1, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:22', NULL);
INSERT INTO `sys_menu` VALUES (17, 15, '0,1,15', '菜单新增', 3, NULL, NULL, NULL, 'system:menu:add', NULL, NULL, 1, 2, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:28', NULL);
INSERT INTO `sys_menu` VALUES (18, 15, '0,1,15', '菜单编辑', 3, NULL, NULL, NULL, 'system:menu:edit', NULL, NULL, 1, 3, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:29', NULL);
INSERT INTO `sys_menu` VALUES (19, 15, '0,1,15', '菜单删除', 3, NULL, NULL, NULL, 'system:menu:delete', NULL, NULL, 1, 4, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:31', NULL);
INSERT INTO `sys_menu` VALUES (20, 1, '0,1', '部门管理', 2, 'Dept', 'dept', 'system/dept/index', NULL, 1, 0, 1, 4, 'OfficeBuilding', NULL, '2025-04-21 10:38:43', '2025-05-23 09:33:42', NULL);
INSERT INTO `sys_menu` VALUES (21, 20, '0,1,20', '部门新增', 3, NULL, NULL, NULL, 'system:dept:add', NULL, NULL, 1, 1, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:28', NULL);
INSERT INTO `sys_menu` VALUES (22, 20, '0,1,20', '部门编辑', 3, NULL, NULL, NULL, 'system:dept:edit', NULL, NULL, 1, 2, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:01:28', NULL);
INSERT INTO `sys_menu` VALUES (23, 20, '0,1,20', '部门删除', 3, NULL, NULL, NULL, 'system:dept:delete', NULL, NULL, 1, 3, NULL, NULL, '2025-04-21 10:38:43', '2025-04-21 11:05:14', NULL);
INSERT INTO `sys_menu` VALUES (25, 20, '0,1,20', '部门查询', 3, NULL, NULL, NULL, 'system:dept:query', 0, 0, 1, 4, NULL, NULL, '2025-05-08 09:52:12', '2025-05-08 09:52:12', NULL);
INSERT INTO `sys_menu` VALUES (30, 0, '0', '内容管理', 1, NULL, '/content', 'Layout', NULL, 0, 0, 1, 3, 'Document', '/content/article', '2025-05-23 14:27:04', '2025-05-23 14:37:20', NULL);
INSERT INTO `sys_menu` VALUES (31, 30, '0,30', '文章管理', 2, 'Article', 'article', 'content/article/index', NULL, 0, 0, 1, 0, 'Tickets', NULL, '2025-05-23 14:29:31', '2025-05-23 14:30:31', NULL);
INSERT INTO `sys_menu` VALUES (32, 30, '0,30', '分类管理', 2, 'Category', 'category', 'content/category/index', NULL, 0, 0, 1, 0, 'Files', NULL, '2025-05-23 14:38:36', '2025-05-23 14:38:36', NULL);
INSERT INTO `sys_menu` VALUES (33, 0, '0', '外链文档', 4, NULL, 'https://www.baidu.com/', NULL, NULL, 0, 0, 1, 4, 'Link', NULL, '2025-05-23 15:26:57', '2025-05-23 15:27:42', NULL);

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

SET FOREIGN_KEY_CHECKS = 1;
