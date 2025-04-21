-- 创建数据库
DROP DATABASE IF EXISTS rbac_admin;
CREATE DATABASE rbac_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE rbac_admin;

-- 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  username VARCHAR(64) NOT NULL COMMENT '用户名',
  password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
  name VARCHAR(64) NOT NULL COMMENT '真实姓名',
  phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  email VARCHAR(64) DEFAULT NULL COMMENT '邮箱',
  dept_id BIGINT DEFAULT NULL COMMENT '部门ID',
  avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-禁用）',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  create_by BIGINT DEFAULT NULL COMMENT '创建者ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by BIGINT DEFAULT NULL COMMENT '更新者ID',
  update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除（1-是，0-否）',
  PRIMARY KEY (id),
  UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 部门表
DROP TABLE IF EXISTS sys_dept;
CREATE TABLE sys_dept (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '部门ID',
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父部门ID',
  name VARCHAR(64) NOT NULL COMMENT '部门名称',
  leader VARCHAR(64) DEFAULT NULL COMMENT '负责人',
  phone VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  email VARCHAR(64) DEFAULT NULL COMMENT '邮箱',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-停用）',
  sort INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  name VARCHAR(64) NOT NULL COMMENT '角色名称',
  code VARCHAR(64) NOT NULL COMMENT '角色编码',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '状态（1-正常，0-停用）',
  remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
  sort INT NOT NULL DEFAULT 0 COMMENT '显示顺序',
  create_by BIGINT DEFAULT NULL COMMENT '创建者ID',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_by BIGINT DEFAULT NULL COMMENT '更新者ID',
  update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除',
  PRIMARY KEY (id),
  UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 菜单表
DROP TABLE IF EXISTS sys_menu;
CREATE TABLE sys_menu (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
  parent_id BIGINT NOT NULL DEFAULT 0 COMMENT '父节点ID',
  tree_path VARCHAR(255) DEFAULT NULL COMMENT '父节点路径',
  name VARCHAR(64) NOT NULL COMMENT '菜单名称',
  type TINYINT NOT NULL COMMENT '菜单类型（1-目录 2-菜单 3-按钮 4-外链）',
  route_name VARCHAR(255) DEFAULT NULL COMMENT '路由名称（Vue Router 命名路由名称）',
  route_path VARCHAR(128) DEFAULT NULL COMMENT '路由路径（Vue Router 中定义的 URL 路径）',
  component VARCHAR(128) DEFAULT NULL COMMENT '组件路径（相对于src/views/，或者后缀.vue）',
  perm VARCHAR(128) DEFAULT NULL COMMENT '[按钮] 权限标识',
  always_show TINYINT DEFAULT NULL COMMENT '[目录] 是否一个路由显示（1-是 0-否）',
  keep_alive TINYINT DEFAULT NULL COMMENT '[菜单] 是否开启页面缓存（1-是 0-否）',
  visible TINYINT DEFAULT 1 COMMENT '显示状态（1-显示 0-隐藏）',
  sort INT DEFAULT 0 COMMENT '排序',
  icon VARCHAR(64) DEFAULT NULL COMMENT '菜单图标',
  redirect VARCHAR(128) DEFAULT NULL COMMENT '跳转路径',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  params VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
  user_id BIGINT NOT NULL COMMENT '用户ID',
  role_id BIGINT NOT NULL COMMENT '角色ID',
  PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色菜单关联表
DROP TABLE IF EXISTS sys_role_menu;
CREATE TABLE sys_role_menu (
  role_id BIGINT NOT NULL COMMENT '角色ID',
  menu_id BIGINT NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';

-- 操作日志表
DROP TABLE IF EXISTS sys_operation_log;
CREATE TABLE sys_operation_log (
  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  module VARCHAR(64) NOT NULL COMMENT '操作模块',
  operation VARCHAR(64) NOT NULL COMMENT '操作类型',
  method VARCHAR(128) NOT NULL COMMENT '方法名称',
  request_url VARCHAR(255) NOT NULL COMMENT '请求URL',
  request_method VARCHAR(16) NOT NULL COMMENT '请求方式',
  request_param TEXT DEFAULT NULL COMMENT '请求参数',
  response_result TEXT DEFAULT NULL COMMENT '返回结果',
  error_msg TEXT DEFAULT NULL COMMENT '错误消息',
  operation_time INT NOT NULL COMMENT '操作耗时（毫秒）',
  user_id BIGINT NOT NULL COMMENT '操作人ID',
  username VARCHAR(64) NOT NULL COMMENT '操作人用户名',
  ip VARCHAR(64) NOT NULL COMMENT 'IP地址',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '操作状态（1-成功，0-失败）',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 插入测试数据

-- 部门数据
INSERT INTO sys_dept (id, parent_id, name, leader, phone, email, status, sort, create_time) VALUES 
(1, 0, '总公司', '张三', '13800138000', 'admin@example.com', 1, 1, NOW()),
(2, 1, '研发部', '李四', '13800138001', 'dev@example.com', 1, 1, NOW()),
(3, 1, '市场部', '王五', '13800138002', 'market@example.com', 1, 2, NOW()),
(4, 1, '财务部', '赵六', '13800138003', 'finance@example.com', 1, 3, NOW()),
(5, 2, '前端组', '钱七', '13800138004', 'frontend@example.com', 1, 1, NOW()),
(6, 2, '后端组', '孙八', '13800138005', 'backend@example.com', 1, 2, NOW());

-- 角色数据
INSERT INTO sys_role (id, name, code, status, remark, sort, create_time) VALUES
(1, '超级管理员', 'ROLE_ADMIN', 1, '系统内置超级管理员角色', 1, NOW()),
(2, '普通用户', 'ROLE_USER', 1, '普通用户角色', 2, NOW()),
(3, '开发人员', 'ROLE_DEV', 1, '开发人员角色', 3, NOW());

-- 用户数据 (密码统一为: 123456，实际应用中应该使用加密后的密码)
INSERT INTO sys_user (id, username, password, name, phone, email, dept_id, status, create_time) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', '13800138000', 'admin@example.com', 1, 1, NOW()),
(2, 'user', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '普通用户', '13800138001', 'user@example.com', 2, 1, NOW()),
(3, 'dev', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '开发人员', '13800138002', 'dev@example.com', 6, 1, NOW());

-- 用户角色关联数据
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 3);

-- 菜单数据
INSERT INTO sys_menu (id, parent_id, name, type, route_name, route_path, component, perm, always_show, keep_alive, visible, sort, icon, redirect, create_time) VALUES
-- 系统管理
(1, 0, '系统管理', 1, 'System', '/system', 'Layout', NULL, 1, 0, 1, 1, 'system', '/system/user', NOW()),

-- 用户管理
(2, 1, '用户管理', 1, 'User', 'user', 'system/user/index', NULL, 1, 0, 1, 1, 'user', NULL, NOW()),
(3, 2, '用户查询', 3, NULL, NULL, NULL, 'system:user:query', NULL, NULL, 1, 1, NULL, NULL, NOW()),
(4, 2, '用户新增', 3, NULL, NULL, NULL, 'system:user:add', NULL, NULL, 1, 2, NULL, NULL, NOW()),
(5, 2, '用户编辑', 3, NULL, NULL, NULL, 'system:user:edit', NULL, NULL, 1, 3, NULL, NULL, NOW()),
(6, 2, '用户删除', 3, NULL, NULL, NULL, 'system:user:delete', NULL, NULL, 1, 4, NULL, NULL, NOW()),
(7, 2, '重置密码', 3, NULL, NULL, NULL, 'system:user:resetPwd', NULL, NULL, 1, 5, NULL, NULL, NOW()),
(8, 2, '用户导入', 3, NULL, NULL, NULL, 'system:user:import', NULL, NULL, 1, 6, NULL, NULL, NOW()),
(9, 2, '用户导出', 3, NULL, NULL, NULL, 'system:user:export', NULL, NULL, 1, 7, NULL, NULL, NOW()),

-- 角色管理
(10, 1, '角色管理', 1, 'Role', 'role', 'system/role/index', NULL, 1, 0, 1, 2, 'role', NULL, NOW()),
(11, 10, '角色查询', 3, NULL, NULL, NULL, 'system:role:query', NULL, NULL, 1, 1, NULL, NULL, NOW()),
(12, 10, '角色新增', 3, NULL, NULL, NULL, 'system:role:add', NULL, NULL, 1, 2, NULL, NULL, NOW()),
(13, 10, '角色编辑', 3, NULL, NULL, NULL, 'system:role:edit', NULL, NULL, 1, 3, NULL, NULL, NOW()),
(14, 10, '角色删除', 3, NULL, NULL, NULL, 'system:role:delete', NULL, NULL, 1, 4, NULL, NULL, NOW()),

-- 菜单管理
(15, 1, '菜单管理', 1, 'Menu', 'menu', 'system/menu/index', NULL, 1, 0, 1, 3, 'menu', NULL, NOW()),
(16, 15, '菜单查询', 3, NULL, NULL, NULL, 'system:menu:query', NULL, NULL, 1, 1, NULL, NULL, NOW()),
(17, 15, '菜单新增', 3, NULL, NULL, NULL, 'system:menu:add', NULL, NULL, 1, 2, NULL, NULL, NOW()),
(18, 15, '菜单编辑', 3, NULL, NULL, NULL, 'system:menu:edit', NULL, NULL, 1, 3, NULL, NULL, NOW()),
(19, 15, '菜单删除', 3, NULL, NULL, NULL, 'system:menu:delete', NULL, NULL, 1, 4, NULL, NULL, NOW());

-- 角色菜单关联数据
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
-- 超级管理员角色拥有所有菜单权限
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12), (1, 13), (1, 14), (1, 15), (1, 16), (1, 17), (1, 18), (1, 19),

-- 普通用户角色拥有查询权限
(2, 1), (2, 2), (2, 3), (2, 10), (2, 11), (2, 15), (2, 16),

-- 开发人员角色拥有部分权限
(3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 10), (3, 11), (3, 15), (3, 16);