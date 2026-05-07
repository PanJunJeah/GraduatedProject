-- 数据库初始化脚本
-- 创建翻译平台专用数据库

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS translatedb
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

-- 连接到 translatedb 数据库后执行以下脚本

-- 用户表已在 JPA Entity 中定义，这里用于手动初始化或验证
-- CREATE TABLE IF NOT EXISTS users (
--     id BIGSERIAL PRIMARY KEY,
--     username VARCHAR(50) UNIQUE NOT NULL,
--     email VARCHAR(100) UNIQUE NOT NULL,
--     password VARCHAR(255) NOT NULL,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- 翻译历史表
-- CREATE TABLE IF NOT EXISTS translation_history (
--     id BIGSERIAL PRIMARY KEY,
--     user_id BIGINT NOT NULL REFERENCES users(id),
--     source_text TEXT NOT NULL,
--     translated_text TEXT NOT NULL,
--     source_lang VARCHAR(10) NOT NULL,
--     target_lang VARCHAR(10) NOT NULL,
--     translation_type VARCHAR(20),
--     context TEXT,
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- 术语库表
-- CREATE TABLE IF NOT EXISTS term_glossary (
--     id BIGSERIAL PRIMARY KEY,
--     user_id BIGINT NOT NULL REFERENCES users(id),
--     source_lang VARCHAR(50) NOT NULL,
--     target_lang VARCHAR(50) NOT NULL,
--     source_term VARCHAR(500) NOT NULL,
--     target_term VARCHAR(500) NOT NULL,
--     description VARCHAR(200),
--     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
-- );

-- 创建索引以提升查询性能
CREATE INDEX IF NOT EXISTS idx_translation_history_user_id ON translation_history(user_id);
CREATE INDEX IF NOT EXISTS idx_translation_history_created_at ON translation_history(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_term_glossary_user_id ON term_glossary(user_id);
CREATE INDEX IF NOT EXISTS idx_term_glossary_lang_pair ON term_glossary(source_lang, target_lang);

-- 插入示例数据（可选）
-- INSERT INTO users (username, email, password) VALUES
--     ('demo', 'demo@example.com', '$2a$10$...');

COMMENT ON DATABASE translatedb IS 'Transformer 交互式翻译平台数据库';
