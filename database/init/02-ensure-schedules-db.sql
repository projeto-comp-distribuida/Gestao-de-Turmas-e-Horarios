-- Garante que o banco distrischool_schedules existe
-- Este script será executado sempre que o container for iniciado

DO $$
BEGIN
    IF NOT EXISTS (SELECT FROM pg_database WHERE datname = 'distrischool_schedules') THEN
        CREATE DATABASE distrischool_schedules;
    END IF;
END
$$;

