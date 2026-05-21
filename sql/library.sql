/*
 Navicat Premium Dump SQL

 Source Server         : Library
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45)
 Source Host           : localhost:3306
 Source Schema         : library

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45)
 File Encoding         : 65001

 Date: 24/04/2026 14:13:57
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
-- Table structure for ai_conversation
-- ----------------------------
DROP TABLE IF EXISTS `ai_conversation`;
CREATE TABLE `ai_conversation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '会话ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会话唯一标识',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '对话摘要（超过10条时生成）',
  `message_count` int NULL DEFAULT 0 COMMENT '消息数量',
  `status` int NULL DEFAULT 1 COMMENT '状态 0-结束 1-活跃',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `session_id`(`session_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI对话会话表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_conversation
-- ----------------------------
INSERT INTO `ai_conversation` VALUES (1, 8537, '093d6f42-0948-4602-ba56-a57cd027450f', NULL, 0, 1, '2026-04-21 18:36:13', '2026-04-21 18:36:13');
INSERT INTO `ai_conversation` VALUES (2, 8537, 'afce1891-8e28-45fd-8501-3defc30d0359', NULL, 6, 1, '2026-04-21 18:36:52', '2026-04-21 18:37:51');
INSERT INTO `ai_conversation` VALUES (3, 8537, '479e100c-db03-4266-8e90-dc7c802dd73f', NULL, 4, 1, '2026-04-21 18:38:20', '2026-04-21 18:38:49');
INSERT INTO `ai_conversation` VALUES (4, 8537, '518a0fdf-9339-4caa-9445-83e921c49bbe', '用户小虎多次询问助手身份，助手反复介绍自己是图书馆智能助手。', 2, 1, '2026-04-21 18:41:39', '2026-04-21 18:44:32');
INSERT INTO `ai_conversation` VALUES (5, 8537, 'f1c69d22-0917-4574-b2d1-0ee409124515', NULL, 8, 1, '2026-04-21 18:45:45', '2026-04-21 18:46:49');
INSERT INTO `ai_conversation` VALUES (6, 8537, '0fb10068-31c0-4d1c-a647-31fba3c874a2', NULL, 0, 1, '2026-04-21 18:47:00', '2026-04-21 18:47:00');
INSERT INTO `ai_conversation` VALUES (7, 8537, '782166c5-6370-415d-aa2e-32e69c83a06e', NULL, 10, 1, '2026-04-21 18:48:17', '2026-04-21 18:50:52');
INSERT INTO `ai_conversation` VALUES (8, 8537, 'd2de9acb-fed0-4141-8404-a7e4608a9ad7', NULL, 0, 1, '2026-04-21 18:53:23', '2026-04-21 18:53:23');
INSERT INTO `ai_conversation` VALUES (9, 1, '30e84453-4271-4354-a0ff-a09ba65704f4', NULL, 8, 1, '2026-04-21 18:53:46', '2026-04-21 19:02:34');
INSERT INTO `ai_conversation` VALUES (10, 2, '8c59c9cc-90d2-405d-bf13-02cedcaaa537', NULL, 8, 1, '2026-04-21 18:54:51', '2026-04-22 17:01:17');
INSERT INTO `ai_conversation` VALUES (11, 1, 'd0d7e885-a3be-46fa-b86a-f9470105eeb6', NULL, 2, 1, '2026-04-21 18:55:28', '2026-04-21 18:55:39');
INSERT INTO `ai_conversation` VALUES (12, 1, '0b27247b-4998-4082-954c-6ce844719f17', NULL, 0, 1, '2026-04-21 18:55:55', '2026-04-21 18:55:55');
INSERT INTO `ai_conversation` VALUES (13, 1, '3ebfd331-1c22-44ec-a3c2-f16ede1f8485', NULL, 0, 1, '2026-04-21 18:55:55', '2026-04-21 18:55:55');
INSERT INTO `ai_conversation` VALUES (14, 2, '49fd9d9a-e068-426f-96f5-e0b4fdeff4a7', NULL, 0, 1, '2026-04-21 19:02:41', '2026-04-21 19:02:41');
INSERT INTO `ai_conversation` VALUES (15, 1, 'c5e45ec8-0df8-44fd-a7d5-2fac1e2c01ae', NULL, 0, 1, '2026-04-23 10:53:47', '2026-04-23 10:53:47');
INSERT INTO `ai_conversation` VALUES (16, 9, '80cb014c-e080-40f3-a438-4a0ed5b8d754', NULL, 0, 1, '2026-04-23 11:00:33', '2026-04-23 11:00:33');
INSERT INTO `ai_conversation` VALUES (17, 1, 'bbbbc9ab-d006-4bfd-acb4-6cf4a2772b11', NULL, 0, 1, '2026-04-23 11:02:55', '2026-04-23 11:02:55');
INSERT INTO `ai_conversation` VALUES (18, 8, 'e1bb68f0-7240-40a8-8681-a27d01e18b05', NULL, 0, 1, '2026-04-23 11:14:10', '2026-04-23 11:14:10');
INSERT INTO `ai_conversation` VALUES (19, 2, 'b8754a90-165b-4bc5-af2c-0e31e0a60075', NULL, 0, 1, '2026-04-23 11:15:11', '2026-04-23 11:15:11');
INSERT INTO `ai_conversation` VALUES (20, 3, 'cc4d14e3-3a7e-40f6-b1b7-b1ab8a2ab5c5', NULL, 0, 1, '2026-04-23 11:21:14', '2026-04-23 11:21:14');
INSERT INTO `ai_conversation` VALUES (21, 3, '2b998a08-f4ed-4916-9587-f9513b743d3e', NULL, 0, 1, '2026-04-23 17:30:16', '2026-04-23 17:30:16');
INSERT INTO `ai_conversation` VALUES (22, 1, 'a80c60e6-69e0-4c94-ba38-609f141e3985', NULL, 0, 1, '2026-04-24 14:12:06', '2026-04-24 14:12:06');

-- ----------------------------
-- Table structure for ai_message
-- ----------------------------
DROP TABLE IF EXISTS `ai_message`;
CREATE TABLE `ai_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `conversation_id` bigint NOT NULL COMMENT '会话ID',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色 user/assistant/system',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `conversation_id`(`conversation_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 59 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_message
-- ----------------------------
INSERT INTO `ai_message` VALUES (1, 2, 'user', '我喜欢科幻小说', '2026-04-21 18:37:05');
INSERT INTO `ai_message` VALUES (2, 2, 'assistant', '1. 书名：《人类简史》 - 作者：尤瓦尔·赫拉利 - 推荐理由：虽然这本书不属于传统意义上的科幻小说，但它从宏观视角探讨了人类历史的演变，包含了许多对未来科技和文明发展的想象，适合喜欢探索未来世界的读者。\n\n2. 书名：《测试》 - 作者：111 - 推荐理由：这本书的分类为“literature”，虽然目前没有更多详细信息，但可以根据其名称推测可能涉及创新或实验性内容，适合对新奇题材感兴趣的读者。\n\n3. 书名：《数据结构与算法》 - 作者：严蔚敏 - 推荐理由：虽然这是一本计算机技术类书籍，但其中涉及的算法和逻辑思维常被科幻作品所借鉴，适合对科技与未来世界感兴趣的人。\n\n4. 书名：《Python编程》 - 作者：Mark Lutz - 推荐理由：编程语言是科幻作品中常见的元素，学习Python可以为创作科幻故事或理解科幻中的技术设定提供帮助。\n\n5. 书名：《JavaScript高级程序设计》 - 作者：Nicholas C. Zakas - 推荐理由：作为一门广泛用于网页开发的编程语言，JavaScript在许多科幻题材的互动网站或虚拟世界中都有应用，适合对科技与未来有浓厚兴趣的读者。', '2026-04-21 18:37:05');
INSERT INTO `ai_message` VALUES (3, 2, 'user', '有什么关于历史的书推荐吗？', '2026-04-21 18:37:10');
INSERT INTO `ai_message` VALUES (4, 2, 'assistant', '关于历史的书有《明朝那些事儿》，作者是当年明月，描述是通俗明朝历史。', '2026-04-21 18:37:10');
INSERT INTO `ai_message` VALUES (5, 2, 'user', '最近有什么新书上架？', '2026-04-21 18:37:51');
INSERT INTO `ai_message` VALUES (6, 2, 'assistant', '根据现有藏书信息，没有提到任何新书上架。建议您向图书馆工作人员咨询最新上架的书籍。', '2026-04-21 18:37:51');
INSERT INTO `ai_message` VALUES (7, 3, 'user', '我叫小虎，你是谁', '2026-04-21 18:38:39');
INSERT INTO `ai_message` VALUES (8, 3, 'assistant', '你好，小虎！我是图书馆的智能助手，可以帮你查找图书或解答相关问题。有什么需要帮助的吗？', '2026-04-21 18:38:39');
INSERT INTO `ai_message` VALUES (9, 3, 'user', '你知道我叫什么吗？', '2026-04-21 18:38:49');
INSERT INTO `ai_message` VALUES (10, 3, 'assistant', '我不知道你叫什么。你可以告诉我你的名字吗？', '2026-04-21 18:38:49');
INSERT INTO `ai_message` VALUES (22, 4, 'assistant', '你好，我是图书馆的智能助手，可以帮你推荐图书和解答关于图书馆藏书的问题。有什么需要帮助的吗？', '2026-04-21 18:44:32');
INSERT INTO `ai_message` VALUES (23, 5, 'user', '我叫小虎，你是谁？', '2026-04-21 18:45:55');
INSERT INTO `ai_message` VALUES (24, 5, 'assistant', '你好，小虎！我是图书馆的智能助手，可以帮你查找图书信息和推荐书籍。有什么需要帮助的吗？', '2026-04-21 18:45:55');
INSERT INTO `ai_message` VALUES (25, 5, 'user', '你知道我叫什么名字吗？', '2026-04-21 18:46:10');
INSERT INTO `ai_message` VALUES (26, 5, 'assistant', '是的，小虎，我记得你叫小虎！有什么需要帮助的吗？', '2026-04-21 18:46:10');
INSERT INTO `ai_message` VALUES (27, 5, 'user', '我喜欢阅读历史小说，有什么推荐的吗？', '2026-04-21 18:46:34');
INSERT INTO `ai_message` VALUES (28, 5, 'assistant', '小虎，你喜欢历史小说的话，可以看看《明朝那些事儿》，这是一本通俗讲解明朝历史的书籍，希望你会喜欢！', '2026-04-21 18:46:34');
INSERT INTO `ai_message` VALUES (29, 5, 'user', '你上次给我推荐的是什么书来着？', '2026-04-21 18:46:49');
INSERT INTO `ai_message` VALUES (30, 5, 'assistant', '小虎，你上次我推荐的是《明朝那些事儿》。', '2026-04-21 18:46:49');
INSERT INTO `ai_message` VALUES (31, 7, 'user', '你还记得我叫什么吗？', '2026-04-21 18:48:52');
INSERT INTO `ai_message` VALUES (32, 7, 'assistant', '我还不知道你的名字呢，你可以告诉我你的名字吗？', '2026-04-21 18:48:52');
INSERT INTO `ai_message` VALUES (33, 7, 'user', '我叫小虎，你是谁？', '2026-04-21 18:49:30');
INSERT INTO `ai_message` VALUES (34, 7, 'assistant', '你好，小虎！我是图书馆的智能助手，可以帮你查找图书信息和推荐书籍。需要我帮你找什么书吗？', '2026-04-21 18:49:30');
INSERT INTO `ai_message` VALUES (35, 7, 'user', '你还记得我叫什么名字吗？', '2026-04-21 18:49:44');
INSERT INTO `ai_message` VALUES (36, 7, 'assistant', '当然记得，你叫小虎！😊 有什么需要我帮忙的吗？', '2026-04-21 18:49:45');
INSERT INTO `ai_message` VALUES (37, 7, 'user', '我喜欢阅读科幻小说', '2026-04-21 18:50:26');
INSERT INTO `ai_message` VALUES (38, 7, 'assistant', '小虎，目前图书馆的藏书中没有科幻小说类的书籍。不过，如果你对其他类型的书感兴趣，比如计算机、文学或历史类书籍，我可以为你推荐一些哦！', '2026-04-21 18:50:26');
INSERT INTO `ai_message` VALUES (39, 7, 'user', '我喜欢历史小说\n', '2026-04-21 18:50:52');
INSERT INTO `ai_message` VALUES (40, 7, 'assistant', '1. 书名：《明朝那些事儿》 - 作者：当年明月 - 推荐理由：这是一本以幽默风趣的方式讲述明朝历史的书籍，适合喜欢历史小说的读者，内容生动有趣，易于阅读。\n2. 书名：《三国演义》 - 作者：罗贯中 - 推荐理由：作为中国四大名著之一，这本书以三国时期的历史为背景，融合了丰富的故事情节和人物形象，是历史小说的经典之作。\n3. 书名：《红楼梦》 - 作者：曹雪芹 - 推荐理由：这部作品不仅是一部文学巨著，也深刻反映了封建社会的历史和文化，适合喜欢历史与文学结合的读者。\n4. 书名：《人类简史》 - 作者：尤瓦尔·赫拉利 - 推荐理由：虽然这本书主要涉及科学领域，但它从历史的角度讲述了人类的发展历程，内容新颖，视角独特，值得一读。\n5. 书名：《数据结构与算法》 - 作者：严蔚敏 - 推荐理由：如果你对历史小说感兴趣，但同时也想了解计算机科学的基础知识，这本书可以为你提供一个全新的视角，帮助你理解技术背后的逻辑。', '2026-04-21 18:50:52');
INSERT INTO `ai_message` VALUES (41, 9, 'user', '我是小龙，你叫什么？', '2026-04-21 18:54:28');
INSERT INTO `ai_message` VALUES (42, 9, 'assistant', '我是图书馆的智能助手，你可以叫我小助。你好，小龙！', '2026-04-21 18:54:28');
INSERT INTO `ai_message` VALUES (43, 9, 'user', '你还记得我叫什么吗？', '2026-04-21 18:54:35');
INSERT INTO `ai_message` VALUES (44, 9, 'assistant', '当然记得，你叫小龙！有什么可以帮你的吗？', '2026-04-21 18:54:35');
INSERT INTO `ai_message` VALUES (45, 10, 'user', '我叫小虎，你叫什么？', '2026-04-21 18:55:01');
INSERT INTO `ai_message` VALUES (46, 10, 'assistant', '我叫图书馆智能助手，很高兴认识你，小虎！有什么需要帮助的吗？', '2026-04-21 18:55:01');
INSERT INTO `ai_message` VALUES (47, 10, 'user', '你还记得我叫什么吗？', '2026-04-21 18:55:11');
INSERT INTO `ai_message` VALUES (48, 10, 'assistant', '当然记得，你叫小虎！有什么需要帮助的吗？', '2026-04-21 18:55:11');
INSERT INTO `ai_message` VALUES (49, 10, 'user', '有什么关于历史的书推荐吗？', '2026-04-21 18:55:17');
INSERT INTO `ai_message` VALUES (50, 10, 'assistant', '小虎，关于历史的书有《明朝那些事儿》，作者是当年明月，描述是通俗明朝历史。这本书很适合你！', '2026-04-21 18:55:17');
INSERT INTO `ai_message` VALUES (51, 11, 'user', '你还记得我叫什么吗？', '2026-04-21 18:55:39');
INSERT INTO `ai_message` VALUES (52, 11, 'assistant', '我还不知道你的名字呢，可以告诉我吗？这样我可以更好地为你服务。', '2026-04-21 18:55:39');
INSERT INTO `ai_message` VALUES (53, 9, 'user', '我喜欢历史书籍，为我推荐一下', '2026-04-21 19:02:24');
INSERT INTO `ai_message` VALUES (54, 9, 'assistant', '小龙，你喜欢历史书籍，可以看看《明朝那些事儿》，这是通俗明朝历史的佳作，库存还有5本。', '2026-04-21 19:02:24');
INSERT INTO `ai_message` VALUES (55, 9, 'user', '你还记得我叫什么吗？', '2026-04-21 19:02:34');
INSERT INTO `ai_message` VALUES (56, 9, 'assistant', '当然记得，你叫小龙！有什么可以帮你的吗？', '2026-04-21 19:02:34');
INSERT INTO `ai_message` VALUES (57, 10, 'user', '我喜欢科幻小说', '2026-04-22 17:01:17');
INSERT INTO `ai_message` VALUES (58, 10, 'assistant', '根据你的偏好，你喜欢科幻小说，但在提供的藏书列表中并没有明确的科幻类书籍。不过，我可以根据现有的书籍推荐一些可能符合你兴趣的图书：\n\n1. 书名：《人类简史》 - 作者：尤瓦尔·赫拉利 - 推荐理由：这本书虽然不是科幻小说，但它探讨了人类历史的宏大主题，包含了许多对未来和科技的思考，可能会激发你对科幻的兴趣。\n\n2. 书名：《数据结构与算法》 - 作者：严蔚敏 - 推荐理由：这本书虽然偏向技术，但它是计算机科学的基础，对于理解许多科幻作品中的科技设定非常有帮助。\n\n3. 书名：《Python编程》 - 作者：Mark Lutz - 推荐理由：学习编程可以让你更好地理解科幻作品中的技术设定，同时也能激发你对未来的想象。\n\n4. 书名：《JavaScript高级程序设计》 - 作者：Nicholas C. Zakas - 推荐理由：这是一本关于前端开发的经典书籍，了解这些技术可以帮助你更好地理解现代科技世界，也许会启发你对科幻的兴趣。\n\n5. 书名：《测试》 - 作者：111 - 推荐理由：虽然这本书的信息不完整，但从名字来看，它可能涉及一些实验或探索性质的内容，这与科幻的主题有一定的相似性。\n\n希望这些推荐能带给你一些启发！如果你有更具体的偏好，也可以告诉我哦，小虎！', '2026-04-22 17:01:17');

-- ----------------------------
-- Table structure for announcement
-- ----------------------------
DROP TABLE IF EXISTS `announcement`;
CREATE TABLE `announcement`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '公告标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '公告内容',
  `priority` int NULL DEFAULT 0 COMMENT '优先级 0-普通 1-重要 2-紧急',
  `status` int NULL DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
  `publisher` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发布人',
  `publish_time` datetime NULL DEFAULT NULL COMMENT '发布时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of announcement
-- ----------------------------
INSERT INTO `announcement` VALUES (1, '欢迎使用智慧图书馆管理系统', '亲爱的读者，欢迎使用我们的图书馆管理系统！祝您阅读愉快。', 2, 1, '系统管理员', '2026-03-21 10:00:00', '2026-03-21 10:00:00', '2026-04-13 11:23:15');
INSERT INTO `announcement` VALUES (2, '测试公告', '这是一条测试公告', 1, 1, '系统管理员', '2026-04-13 11:23:51', '2026-04-13 11:23:51', '2026-04-13 11:23:51');
INSERT INTO `announcement` VALUES (3, '测试公告', '这是一条测试公告', 1, 1, '系统管理员', '2026-04-13 11:23:51', '2026-04-13 11:23:51', '2026-04-13 11:23:51');

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
INSERT INTO `book` VALUES (1, 'JavaScript高级程序设计', 'Nicholas C. Zakas', '计算机', '人民邮电出版社', '2020-01-01', 5, 0, 'JavaScript经典教材', '2026-03-04 20:43:26', '2026-04-16 16:17:00');
INSERT INTO `book` VALUES (2, 'Vue.js实战', '梁灏', '计算机', '清华大学出版社', '2019-06-01', 4, 0, 'Vue.js入门到精通', '2026-03-04 20:43:26', '2026-04-16 16:01:20');
INSERT INTO `book` VALUES (3, 'Python编程', 'Mark Lutz', '计算机', '中国电力出版社', '2019-03-01', 7, 0, 'Python经典教程', '2026-03-04 20:43:26', '2026-03-12 21:25:19');
INSERT INTO `book` VALUES (4, '红楼梦', '曹雪芹', '文学', '人民文学出版社', '2018-01-01', 9, 0, '中国古典四大名著之一', '2026-03-04 20:43:26', '2026-03-21 19:55:47');
INSERT INTO `book` VALUES (5, '明朝那些事儿', '当年明月', '历史', '中国友谊出版公司', '2017-05-01', 5, 0, '通俗明朝历史', '2026-03-04 20:43:26', '2026-03-12 21:50:49');
INSERT INTO `book` VALUES (6, '数据结构与算法', '严蔚敏', '计算机', '清华大学出版社', '2018-09-01', 3, 0, '数据结构经典教材', '2026-03-04 20:43:26', '2026-03-12 21:59:23');
INSERT INTO `book` VALUES (7, '三国演义', '罗贯中', '文学', '人民文学出版社', '2019-01-01', 6, 0, '中国古典四大名著之一', '2026-03-04 20:43:26', '2026-04-23 17:29:55');
INSERT INTO `book` VALUES (8, '人类简史', '尤瓦尔·赫拉利', '科学', '中信出版社', '2016-11-01', 4, 0, '从动物到上帝', '2026-03-04 20:43:26', '2026-03-12 21:51:35');
INSERT INTO `book` VALUES (9, '测试', '111', 'literature', '222', '2026-03-02', 5, 1, '无', '2026-03-04 20:48:40', '2026-04-16 15:43:17');

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
INSERT INTO `borrow` VALUES (1, 1, 3, '2024-01-10', '2024-02-10', '2026-04-16', 1, '2026-03-04 20:43:26', '2026-04-16 16:17:00');
INSERT INTO `borrow` VALUES (2, 2, 4, '2024-01-05', '2024-02-05', '2026-04-16', 1, '2026-03-04 20:43:26', '2026-04-16 16:01:20');
INSERT INTO `borrow` VALUES (3, 3, 6, '2024-01-01', '2024-01-31', NULL, 1, '2026-03-04 20:43:26', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (4, 1, 4, '2026-03-04', '2026-04-03', NULL, 0, '2026-03-04 22:17:36', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (5, 3, 4, '2026-03-12', '2026-04-11', NULL, 0, '2026-03-12 21:25:19', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (6, 5, 6, '2026-03-12', '2026-04-11', NULL, 0, '2026-03-12 21:50:49', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (7, 8, 6, '2026-03-12', '2026-04-11', NULL, 0, '2026-03-12 21:51:35', '2026-03-21 20:24:56');
INSERT INTO `borrow` VALUES (8, 7, 6, '2026-03-12', '2026-04-11', '2026-04-23', 1, '2026-03-12 21:58:32', '2026-04-23 17:29:55');
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
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '权限表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_permission
-- ----------------------------
INSERT INTO `sys_role_permission` VALUES (1, 1, 1);
INSERT INTO `sys_role_permission` VALUES (3, 1, 3);
INSERT INTO `sys_role_permission` VALUES (4, 1, 4);
INSERT INTO `sys_role_permission` VALUES (6, 1, 6);
INSERT INTO `sys_role_permission` VALUES (7, 2, 1);
INSERT INTO `sys_role_permission` VALUES (8, 2, 2);
INSERT INTO `sys_role_permission` VALUES (9, 2, 5);
INSERT INTO `sys_role_permission` VALUES (10, 2, 6);
INSERT INTO `sys_role_permission` VALUES (11, 3, 5);
INSERT INTO `sys_role_permission` VALUES (12, 3, 6);
INSERT INTO `sys_role_permission` VALUES (13, 1, 1);
INSERT INTO `sys_role_permission` VALUES (15, 1, 3);
INSERT INTO `sys_role_permission` VALUES (16, 1, 4);
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
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统设置表' ROW_FORMAT = DYNAMIC;

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
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'sysadmin', 'admin123', '系统管理员', 0, '13800138000', 'admin@library.com', 0, '2026-03-05 15:55:47', '2026-03-21 20:58:26', 'https://liuxiang1234.oss-cn-beijing.aliyuncs.com/avatars/cd323ecc-39b5-4775-9d2e-1d5bf762cbf3.jpg');
INSERT INTO `sys_user` VALUES (2, 'libadmin', 'admin123', '图书管理员', 1, '13800138001', 'libadmin@library.com', 0, '2026-03-05 15:55:47', '2026-03-12 20:33:25', 'https://liuxiang1234.oss-cn-beijing.aliyuncs.com/avatars/f26d732f-dd5a-4421-9a42-44d6f931d0f7.jpg');
INSERT INTO `sys_user` VALUES (3, 'zhangsan', '123456', '张三', 1, '13800138002', 'zhangsan@library.com', 0, '2026-03-05 15:55:47', '2026-04-23 17:30:10', 'https://liuxiang1234.oss-cn-beijing.aliyuncs.com/avatars/01abe9b7-b6bd-4a58-b3e6-b22511eccad0.jpg');
INSERT INTO `sys_user` VALUES (4, 'lisi', '123456', '李四', 0, '13800138003', 'lisi@library.com', 0, '2026-03-05 15:55:47', '2026-03-05 15:55:47', NULL);
INSERT INTO `sys_user` VALUES (6, 'wangwu', '123456', '王五', 1, '13800138002', 'wangwu@example.com', 0, '2026-03-21 20:24:56', '2026-03-21 20:24:56', NULL);
INSERT INTO `sys_user` VALUES (7, 'zhaoliu', '123456', '赵六', 0, '13800138003', 'zhaoliu@example.com', 0, '2026-03-21 20:24:56', '2026-03-21 20:24:56', NULL);
INSERT INTO `sys_user` VALUES (8, '测试用户2', '123456', 'test2', 0, '18179658592', '325.lxxxx@gmail.com', 0, '2026-03-21 20:24:56', '2026-04-23 11:12:09', NULL);
INSERT INTO `sys_user` VALUES (9, 'test_user2', '123456', 'test_user2', 0, '', '', 0, '2026-04-23 10:59:43', '2026-04-23 10:59:43', NULL);

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
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户角色关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2, 2);
INSERT INTO `sys_user_role` VALUES (4, 4, 3);
INSERT INTO `sys_user_role` VALUES (6, 6, 3);
INSERT INTO `sys_user_role` VALUES (7, 7, 3);
INSERT INTO `sys_user_role` VALUES (8, 9, 3);
INSERT INTO `sys_user_role` VALUES (9, 8, 2);
INSERT INTO `sys_user_role` VALUES (11, 3, 3);

-- ----------------------------
-- Table structure for user_bak
-- ----------------------------
DROP TABLE IF EXISTS `user_bak`;
CREATE TABLE `user_bak`  (
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
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_bak
-- ----------------------------
INSERT INTO `user_bak` VALUES (1, 'zhangsan', '123456', 'reader', '张三', 1, '13800138000', 'zhangsan@example.com', 0, '2026-03-04 20:43:26', '2026-03-05 14:33:38');
INSERT INTO `user_bak` VALUES (2, 'lisi', '123456', 'reader', '李四', 0, '13800138001', 'lisi@example.com', 0, '2026-03-04 20:43:26', '2026-03-05 14:33:38');
INSERT INTO `user_bak` VALUES (3, 'wangwu', '123456', 'reader', '王五', 1, '13800138002', 'wangwu@example.com', 0, '2026-03-04 20:43:26', '2026-04-16 15:46:08');
INSERT INTO `user_bak` VALUES (4, 'zhaoliu', NULL, 'reader', '赵六', 0, '13800138003', 'zhaoliu@example.com', 0, '2026-03-04 20:43:26', '2026-03-04 20:43:26');
INSERT INTO `user_bak` VALUES (5, 'sunqi', NULL, 'reader', '孙七', 1, '13800138004', 'sunqi@example.com', 1, '2026-03-04 20:43:26', '2026-03-04 21:59:27');
INSERT INTO `user_bak` VALUES (6, 'test_user', NULL, 'reader', '测试用户', 0, '11111111', 'test@qq.com', 0, '2026-03-04 22:30:56', '2026-03-04 22:30:56');
INSERT INTO `user_bak` VALUES (8, '测试用户2', NULL, 'librarian', 'test2', 0, '18179658592', '325.lxxxx@gmail.com', 0, '2026-04-16 15:47:49', '2026-04-23 11:12:09');

SET FOREIGN_KEY_CHECKS = 1;
