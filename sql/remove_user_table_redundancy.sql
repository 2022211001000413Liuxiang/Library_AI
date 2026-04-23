-- 数据库冗余整改脚本：将 user 表重命名为 user_bak 备份
-- 执行前请确保已备份数据！

-- 1. 将 user 表重命名为 user_bak（保留备份）
RENAME TABLE user TO user_bak;

-- 2. 验证 sys_user 表结构和数据是否完整
-- SELECT COUNT(*) FROM sys_user;

-- 3. 如需恢复（慎用）：
-- RENAME TABLE user_bak TO user;
