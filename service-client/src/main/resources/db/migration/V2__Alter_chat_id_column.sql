-- V2__Alter_chat_id_column.sql
ALTER TABLE IF EXISTS users DROP COLUMN IF EXISTS chat_id;

ALTER TABLE IF EXISTS users ADD COLUMN IF NOT EXISTS chat_id BIGINT NOT NULL;

ALTER TABLE IF EXISTS users ADD CONSTRAINT unique_chat_id UNIQUE (chat_id);