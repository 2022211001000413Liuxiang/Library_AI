-- 图书馆管理系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS library DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE library;

-- 图书表
DROP TABLE IF EXISTS book;
CREATE TABLE book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '书名',
    author VARCHAR(50) COMMENT '作者',
    category VARCHAR(20) COMMENT '分类',
    publisher VARCHAR(50) COMMENT '出版社',
    publish_date DATE COMMENT '出版日期',
    stock INT DEFAULT 0 COMMENT '库存',
    status TINYINT DEFAULT 0 COMMENT '状态(0:可借,1:已借出)',
    description TEXT COMMENT '简介',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 用户表
DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    name VARCHAR(50) COMMENT '姓名',
    gender TINYINT DEFAULT 0 COMMENT '性别(0:男,1:女)',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    status TINYINT DEFAULT 0 COMMENT '状态(0:正常,1:禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 借阅表
DROP TABLE IF EXISTS borrow;
CREATE TABLE borrow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    book_id BIGINT NOT NULL COMMENT '图书ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    borrow_date DATE NOT NULL COMMENT '借阅日期',
    due_date DATE NOT NULL COMMENT '应还日期',
    return_date DATE COMMENT '归还日期',
    status TINYINT DEFAULT 0 COMMENT '状态(0:借阅中,1:已归还,2:逾期)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (book_id) REFERENCES book(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅表';

-- 插入示例数据
-- 图书数据
INSERT INTO book (name, author, category, publisher, publish_date, stock, status, description) VALUES
('JavaScript高级程序设计', 'Nicholas C. Zakas', '计算机', '人民邮电出版社', '2020-01-01', 5, 0, 'JavaScript经典教材'),
('Vue.js实战', '梁灏', '计算机', '清华大学出版社', '2019-06-01', 3, 0, 'Vue.js入门到精通'),
('Python编程', 'Mark Lutz', '计算机', '中国电力出版社', '2019-03-01', 8, 0, 'Python经典教程'),
('红楼梦', '曹雪芹', '文学', '人民文学出版社', '2018-01-01', 10, 0, '中国古典四大名著之一'),
('明朝那些事儿', '当年明月', '历史', '中国友谊出版公司', '2017-05-01', 6, 0, '通俗明朝历史'),
('数据结构与算法', '严蔚敏', '计算机', '清华大学出版社', '2018-09-01', 4, 0, '数据结构经典教材'),
('三国演义', '罗贯中', '文学', '人民文学出版社', '2019-01-01', 7, 0, '中国古典四大名著之一'),
('人类简史', '尤瓦尔·赫拉利', '科学', '中信出版社', '2016-11-01', 5, 0, '从动物到上帝');

-- 用户数据
INSERT INTO user (username, name, gender, phone, email, status) VALUES
('zhangsan', '张三', 0, '13800138000', 'zhangsan@example.com', 0),
('lisi', '李四', 0, '13800138001', 'lisi@example.com', 0),
('wangwu', '王五', 1, '13800138002', 'wangwu@example.com', 0),
('zhaoliu', '赵六', 0, '13800138003', 'zhaoliu@example.com', 0),
('sunqi', '孙七', 1, '13800138004', 'sunqi@example.com', 0);

-- 借阅数据
INSERT INTO borrow (book_id, user_id, borrow_date, due_date, status) VALUES
(1, 1, '2024-01-10', '2024-02-10', 0),
(2, 2, '2024-01-05', '2024-02-05', 0),
(3, 3, '2024-01-01', '2024-01-31', 1);
