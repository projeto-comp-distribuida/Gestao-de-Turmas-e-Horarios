-- Script para criar todos os bancos de dados necessários para os microserviços
-- Este script garante que todos os bancos existam antes dos serviços iniciarem
-- NOTA: CREATE DATABASE não pode ser executado dentro de DO blocks, então usamos comandos SQL diretos

-- Criar banco distrischool_auth (se não existir)
SELECT 'CREATE DATABASE distrischool_auth'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'distrischool_auth')\gexec

-- Criar banco distrischool_students (se não existir)
SELECT 'CREATE DATABASE distrischool_students'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'distrischool_students')\gexec

-- Criar banco distrischool_teachers (se não existir)
SELECT 'CREATE DATABASE distrischool_teachers'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'distrischool_teachers')\gexec
