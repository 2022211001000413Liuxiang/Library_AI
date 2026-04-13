/*
 Navicat Premium Dump SQL

 Source Server         : AI-Library
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : library

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 12/04/2026 20:03:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色(admin:系统管理员,librarian:图书管理员)',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态(0:正常,1:禁用)',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'sysadmin', 'admin123', '系统管理员', 'admin', 0, NULL, '2026-03-05 14:33:51', '2026-03-05 14:33:51');
INSERT INTO `admin` VALUES (2, 'libadmin', 'admin123', '图书管理员', 'librarian', 0, NULL, '2026-03-05 14:33:51', '2026-03-05 14:33:51');

-- ----------------------------
-- Table structure for book
-- ----------------------------
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '书名',
  `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '作者',
  `category` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '分类',
  `publisher` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '出版社',
  `publish_date` date NULL DEFAULT NULL COMMENT '出版日期',
  `stock` int NULL DEFAULT 0 COMMENT '库存',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态(0:可借,1:已借出)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '简介',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '图书表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of book
-- ----------------------------
INSERT INTO `book` VALUES (1, 'JavaScript高级程序设计', 'Nicholas C. Zakas', '计算机', '人民邮电出版社', '2020-01-01', 4, 0, 'JavaScript经典教材', '2026-03-04 20:43:26', '2026-03-04 22:17:36');
INSERT INTO `book` VALUES (2, 'Vue.js实战', '梁灏', '计算机', '清华大学出版社', '2019-06-01', 3, 0, 'Vue.js入门到精通', '2026-03-04 20:43:26', '2026-03-04 20:43:26');
INSERT INTO `book` VALUES (3, 'Python编程', 'Mark Lutz', '计算机', '中国电力出版社', '2019-03-01', 7, 0, 'Python经典教程', '2026-03-04 20:43:26', '2026-03-12 21:25:19');
INSERT INTO `book` VALUES (4, '红楼梦', '曹雪芹', '文学', '人民文学出版社', '2018-01-01', 9, 0, '中国古典四大名著之一', '2026-03-04 20:43:26', '2026-03-21 19:55:47');
INSERT INTO `book` VALUES (5, '明朝那些事儿', '当年明月', '历史', '中国友谊出版公司', '2017-05-01', 5, 0, '通俗明朝历史', '2026-03-04 20:43:26', '2026-03-12 21:50:49');
INSERT INTO `book` VALUES (6, '数据结构与算法', '严蔚敏', '计算机', '清华大学出版社', '2018-09-01', 3, 0, '数据结构经典教材', '2026-03-04 20:43:26', '2026-03-12 21:59:23');
INSERT INTO `book` VALUES (7, '三国演义', '罗贯中', '文学', '人民文学出版社', '2019-01-01', 5, 0, '中国古典四大名著之一', '2026-03-04 20:43:26', '2026-03-25 18:40:28');
INSERT INTO `book` VALUES (8, '人类简史', '尤瓦尔·赫拉利', '科学', '中信出版社', '2016-11-01', 4, 0, '从动物到上帝', '2026-03-04 20:43:26', '2026-03-12 21:51:35');
INSERT INTO `book` VALUES (9, '测试', '111', 'literature', '222', '2026-03-02', 0, 1, '无', '2026-03-04 20:48:40', '2026-03-25 18:43:22');

-- ----------------------------
-- Table structure for borrow
-- ----------------------------
DROP TABLE IF EXISTS `borrow`;
CREATE TABLE `borrow`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `book_id` bigint NOT NULL COMMENT '图书ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `borrow_date` date NOT NULL COMMENT '借阅日期',
  `due_date` date NOT NULL COMMENT '应还日期',
  `return_date` date NULL DEFAULT NULL COMMENT '归还日期',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态(0:借阅中,1:已归还,2:逾期)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `book_id`(`book_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `borrow_ibfk_1` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `borrow_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '借阅表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of borrow
-- ----------------------------
INSERT INTO `borrow` VALUES (1, 1, 3, '2024-01-10', '2024-02-10', NULL, 0, '2026-03-04 20:43:26', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (2, 2, 4, '2024-01-05', '2024-02-05', NULL, 0, '2026-03-04 20:43:26', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (3, 3, 6, '2024-01-01', '2024-01-31', NULL, 1, '2026-03-04 20:43:26', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (4, 1, 4, '2026-03-04', '2026-04-03', NULL, 0, '2026-03-04 22:17:36', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (5, 3, 4, '2026-03-12', '2026-04-11', NULL, 0, '2026-03-12 21:25:19', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (6, 5, 6, '2026-03-12', '2026-04-11', NULL, 0, '2026-03-12 21:50:49', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (7, 8, 6, '2026-03-12', '2026-04-11', NULL, 0, '2026-03-12 21:51:35', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (8, 7, 6, '2026-03-12', '2026-04-11', NULL, 0, '2026-03-12 21:58:32', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (9, 6, 6, '2026-03-12', '2026-04-11', NULL, 0, '2026-03-12 21:59:23', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (10, 4, 6, '2026-03-21', '2026-04-20', NULL, 0, '2026-03-21 19:55:47', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (12, 7, 2, '2026-03-25', '2026-04-24', NULL, 0, '2026-03-25 18:40:28', '2026-03-25 18:40:28');
INSERT INTO `borrow` VALUES (13, 9, 3, '2026-03-25', '2026-04-24', NULL, 0, '2026-03-25 18:43:22', '2026-03-25 18:43:22');

-- ----------------------------
-- Table structure for sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_permission`;
CREATE TABLE `sys_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `perm_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限标识',
  `perm_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_perm_key`(`perm_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_permission
-- ----------------------------
INSERT INTO `sys_permission` VALUES (1, 'book:manage', '图书管理', '增删改查图书', '2026-03-05 15:55:47');
INSERT INTO `sys_permission` VALUES (2, 'borrow:manage', '借阅管理', '管理所有借阅记录', '2026-03-05 15:55:47');
INSERT INTO `sys_permission` VALUES (3, 'user:manage', '用户管理', '管理系统用户', '2026-03-05 15:55:47');
INSERT INTO `sys_permission` VALUES (4, 'settings:manage', '系统设置', '系统配置', '2026-03-05 15:55:47');
INSERT INTO `sys_permission` VALUES (5, 'borrow:self', '我的借阅', '查看自己的借阅', '2026-03-05 15:55:47');
INSERT INTO `sys_permission` VALUES (6, 'profile:view', '查看个人信息', '查看和编辑个人信息', '2026-03-05 15:55:47');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色标识',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态(0:正常,1:禁用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_role_key`(`role_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'admin', '系统管理员', '拥有所有权限', 0, '2026-03-05 15:55:47', '2026-03-05 15:55:47');
INSERT INTO `sys_role` VALUES (2, 'librarian', '图书管理员', '管理图书和借阅', 0, '2026-03-05 15:55:47', '2026-03-05 15:55:47');
INSERT INTO `sys_role` VALUES (3, 'reader', '读者', '借阅图书', 0, '2026-03-05 15:55:47', '2026-03-05 15:55:47');

-- ----------------------------
-- Table structure for sys_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_permission`;
CREATE TABLE `sys_role_permission`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `perm_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_perm_id`(`perm_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 1, 1);
INSERT INTO `sys_role_permission` VALUES (2, 1, 2);
INSERT INTO `sys_role_permission` VALUES (3, 1, 3);
INSERT INTO `sys_role_permission` VALUES (4, 1, 4);
INSERT INTO `sys_role_permission` VALUES (5, 1, 5);
INSERT INTO `sys_role_permission` VALUES (6, 1, 6);
INSERT INTO `sys_role_permission` VALUES (7, 2, 1);
INSERT INTO `sys_role_permission` VALUES (8, 2, 2);
INSERT INTO `sys_role_permission` VALUES (9, 2, 5);
INSERT INTO `sys_role_permission` VALUES (10, 2, 6);
INSERT INTO `sys_role_permission` VALUES (11, 3, 5);
INSERT INTO `sys_role_permission` VALUES (12, 3, 6);
INSERT INTO `sys_role_permission` VALUES (13, 1, 1);
INSERT INTO `sys_role_permission` VALUES (14, 1, 2);
INSERT INTO `sys_role_permission` VALUES (15, 1, 3);
INSERT INTO `sys_role_permission` VALUES (16, 1, 4);
INSERT INTO `sys_role_permission` VALUES (17, 1, 5);
INSERT INTO `sys_role_permission` VALUES (18, 1, 6);
INSERT INTO `sys_role_permission` VALUES (19, 2, 1);
INSERT INTO `sys_role_permission` VALUES (20, 2, 2);
INSERT INTO `sys_role_permission` VALUES (21, 2, 5);
INSERT INTO `sys_role_permission` VALUES (22, 2, 6);
INSERT INTO `sys_role_permission` VALUES (23, 3, 5);
INSERT INTO `sys_role_permission` VALUES (24, 3, 6);

-- ----------------------------
-- Table structure for sys_settings
-- ----------------------------
DROP TABLE IF EXISTS `sys_settings`;
CREATE TABLE `sys_settings`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `setting_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设置项键名',
  `setting_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '设置项值',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设置项描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `setting_key`(`setting_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统设置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_settings
-- ----------------------------
INSERT INTO `sys_settings` VALUES (1, 'library_name', '智慧图书馆', '图书馆名称', '2026-03-21 19:42:24', '2026-03-21 19:42:24');
INSERT INTO `sys_settings` VALUES (2, 'max_borrow_days', '30', '最大借阅天数', '2026-03-21 19:42:24', '2026-03-21 19:42:24');
INSERT INTO `sys_settings` VALUES (3, 'max_borrow_count', '10', '最大借阅数量', '2026-03-21 19:42:24', '2026-03-21 19:42:24');
INSERT INTO `sys_settings` VALUES (4, 'overdue_fine_per_day', '0.5', '逾期罚款(元/天)', '2026-03-21 19:42:24', '2026-03-21 19:42:24');
INSERT INTO `sys_settings` VALUES (5, 'open_time', '08:00', '开馆时间', '2026-03-21 19:42:24', '2026-03-21 19:42:24');
INSERT INTO `sys_settings` VALUES (6, 'close_time', '22:00', '闭馆时间', '2026-03-21 19:42:24', '2026-03-21 19:42:24');
INSERT INTO `sys_settings` VALUES (7, 'contact_phone', '400-123-4567', '联系电话', '2026-03-21 19:42:24', '2026-03-21 19:42:24');
INSERT INTO `sys_settings` VALUES (8, 'contact_email', 'library@example.com', '联系邮箱', '2026-03-21 19:42:24', '2026-03-21 19:42:24');
INSERT INTO `sys_settings` VALUES (9, 'notice', '欢迎使用智慧图书馆管理系统', '系统公告', '2026-03-21 19:42:24', '2026-03-21 19:42:24');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别(0:男,1:女)',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态(0:正常,1:禁用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '头像URL',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'sysadmin', 'admin123', '系统管理员', 0, '13800138000', 'admin@library.com', 0, '2026-03-05 15:55:47', '2026-03-21 20:58:26', 'https://liuxiang1234.oss-cn-beijing.aliyuncs.com/avatars/cd323ecc-39b5-4775-9d2e-1d5bf762cbf3.jpg');
INSERT INTO `sys_user` VALUES (2, 'libadmin', 'admin123', '图书管理员', 1, '13800138001', 'libadmin@library.com', 0, '2026-03-05 15:55:47', '2026-03-12 20:33:25', 'https://liuxiang1234.oss-cn-beijing.aliyuncs.com/avatars/f26d732f-dd5a-4421-9a42-44d6f931d0f7.jpg');
INSERT INTO `sys_user` VALUES (3, 'zhangsan', '123456', '张三', 1, '13800138002', 'zhangsan@library.com', 0, '2026-03-05 15:55:47', '2026-03-21 20:58:26', 'https://liuxiang1234.oss-cn-beijing.aliyuncs.com/avatars/01abe9b7-b6bd-4a58-b3e6-b22511eccad0.jpg');
INSERT INTO `sys_user` VALUES (4, 'lisi', '123456', '李四', 0, '13800138003', 'lisi@library.com', 0, '2026-03-05 15:55:47', '2026-03-05 15:55:47', NULL);
INSERT INTO `sys_user` VALUES (6, 'wangwu', '123456', '王五', 1, '13800138002', 'wangwu@example.com', 0, '2026-03-21 20:24:56', '2026-03-21 20:24:56', NULL);
INSERT INTO `sys_user` VALUES (7, 'zhaoliu', '123456', '赵六', 0, '13800138003', 'zhaoliu@example.com', 0, '2026-03-21 20:24:56', '2026-03-21 20:24:56', NULL);
INSERT INTO `sys_user` VALUES (8, 'test_user', '123456', '测试用户', 0, '11111111', 'test@qq.com', 0, '2026-03-21 20:24:56', '2026-03-21 20:24:56', NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2, 2);
INSERT INTO `sys_user_role` VALUES (3, 3, 3);
INSERT INTO `sys_user_role` VALUES (4, 4, 3);
INSERT INTO `sys_user_role` VALUES (5, 8, 3);
INSERT INTO `sys_user_role` VALUES (6, 6, 3);
INSERT INTO `sys_user_role` VALUES (7, 7, 3);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'reader' COMMENT '角色(reader:读者,librarian:图书管理员,admin:系统管理员)',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '姓名',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别(0:男,1:女)',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电话',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态(0:正常,1:禁用)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'zhangsan', '123456', 'reader', '张三', 1, '13800138000', 'zhangsan@example.com', 0, '2026-03-04 20:43:26', '2026-03-05 14:33:38');
INSERT INTO `user` VALUES (2, 'lisi', '123456', 'reader', '李四', 0, '13800138001', 'lisi@example.com', 0, '2026-03-04 20:43:26', '2026-03-05 14:33:38');
INSERT INTO `user` VALUES (3, 'wangwu', '123456', 'reader', '王五', 1, '13800138002', 'wangwu@example.com', 0, '2026-03-04 20:43:26', '2026-03-05 14:33:38');
INSERT INTO `user` VALUES (4, 'zhaoliu', NULL, 'reader', '赵六', 0, '13800138003', 'zhaoliu@example.com', 0, '2026-03-04 20:43:26', '2026-03-04 20:43:26');
INSERT INTO `user` VALUES (5, 'sunqi', NULL, 'reader', '孙七', 1, '13800138004', 'sunqi@example.com', 1, '2026-03-04 20:43:26', '2026-03-04 21:59:27');
INSERT INTO `user` VALUES (6, 'test_user', NULL, 'reader', '测试用户', 0, '11111111', 'test@qq.com', 0, '2026-03-04 22:30:56', '2026-03-04 22:30:56');

SET FOREIGN_KEY_CHECKS = 1;
