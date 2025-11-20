-- Migração V3: Inserir turnos padrão (shifts)
-- V3__Insert_default_shifts.sql

-- Inserir turnos padrão se não existirem
-- Usando verificação condicional para evitar duplicatas

-- Turno Manhã (07:00 - 12:00)
INSERT INTO shifts (name, description, start_time, end_time, active, created_at, updated_at)
SELECT 'Manhã', 'Turno da Manhã', '07:00:00'::TIME, '12:00:00'::TIME, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE name = 'Manhã');

-- Turno Tarde (13:00 - 18:00)
INSERT INTO shifts (name, description, start_time, end_time, active, created_at, updated_at)
SELECT 'Tarde', 'Turno da Tarde', '13:00:00'::TIME, '18:00:00'::TIME, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE name = 'Tarde');

-- Turno Noite (18:00 - 22:00)
INSERT INTO shifts (name, description, start_time, end_time, active, created_at, updated_at)
SELECT 'Noite', 'Turno da Noite', '18:00:00'::TIME, '22:00:00'::TIME, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE name = 'Noite');

-- Turno Integral (07:00 - 18:00)
INSERT INTO shifts (name, description, start_time, end_time, active, created_at, updated_at)
SELECT 'Integral', 'Turno Integral', '07:00:00'::TIME, '18:00:00'::TIME, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM shifts WHERE name = 'Integral');

