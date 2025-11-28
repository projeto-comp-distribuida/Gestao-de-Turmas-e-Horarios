-- Migração V4: Adicionar coluna subject_id à tabela classes
-- V4__Add_subject_to_classes.sql

-- Adicionar coluna subject_id à tabela classes
ALTER TABLE classes ADD COLUMN IF NOT EXISTS subject_id BIGINT REFERENCES subjects(id);

-- Criar índice para melhorar performance de consultas
CREATE INDEX IF NOT EXISTS idx_class_subject ON classes(subject_id);




