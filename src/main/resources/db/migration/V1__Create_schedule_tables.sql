-- Migração inicial para criar todas as tabelas do módulo de Gestão de Turmas e Horários
-- V1__Create_schedule_tables.sql

-- Tabela de Escolas
CREATE TABLE IF NOT EXISTS schools (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE,
    address VARCHAR(500),
    phone VARCHAR(20),
    email VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by VARCHAR(255)
);

CREATE INDEX idx_school_code ON schools(code);
CREATE INDEX idx_school_active ON schools(active);

-- Tabela de Turnos
CREATE TABLE IF NOT EXISTS shifts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by VARCHAR(255)
);

CREATE INDEX idx_shift_name ON shifts(name);

-- Tabela de Disciplinas
CREATE TABLE IF NOT EXISTS subjects (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50),
    workload_hours INTEGER,
    description TEXT,
    school_id BIGINT REFERENCES schools(id),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by VARCHAR(255)
);

CREATE INDEX idx_subject_code ON subjects(code);
CREATE INDEX idx_subject_school ON subjects(school_id);

-- Tabela de Turmas
CREATE TABLE IF NOT EXISTS classes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50),
    academic_year VARCHAR(10),
    period VARCHAR(50),
    capacity INTEGER,
    current_students INTEGER DEFAULT 0,
    school_id BIGINT NOT NULL REFERENCES schools(id),
    shift_id BIGINT REFERENCES shifts(id),
    start_date DATE,
    end_date DATE,
    student_ids TEXT,
    teacher_ids TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by VARCHAR(255)
);

CREATE INDEX idx_class_code ON classes(code);
CREATE INDEX idx_class_school ON classes(school_id);
CREATE INDEX idx_class_shift ON classes(shift_id);
CREATE INDEX idx_class_academic_year ON classes(academic_year);

-- Tabela de Horários
CREATE TABLE IF NOT EXISTS schedules (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT NOT NULL REFERENCES classes(id),
    subject_id BIGINT NOT NULL REFERENCES subjects(id),
    shift_id BIGINT REFERENCES shifts(id),
    day_of_week VARCHAR(20) NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    room VARCHAR(100),
    teacher_id BIGINT,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by VARCHAR(255)
);

CREATE INDEX idx_schedule_class ON schedules(class_id);
CREATE INDEX idx_schedule_subject ON schedules(subject_id);
CREATE INDEX idx_schedule_shift ON schedules(shift_id);
CREATE INDEX idx_schedule_day_time ON schedules(day_of_week, start_time, end_time);
CREATE INDEX idx_schedule_room ON schedules(room);
CREATE INDEX idx_schedule_teacher ON schedules(teacher_id);

-- Tabela de Feriados
CREATE TABLE IF NOT EXISTS holidays (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    type VARCHAR(50) NOT NULL,
    description TEXT,
    recurring BOOLEAN NOT NULL DEFAULT false,
    school_id BIGINT REFERENCES schools(id),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by VARCHAR(255)
);

CREATE INDEX idx_holiday_date ON holidays(date);
CREATE INDEX idx_holiday_school ON holidays(school_id);
CREATE INDEX idx_holiday_type ON holidays(type);

-- Tabela de Calendário Acadêmico
CREATE TABLE IF NOT EXISTS academic_calendars (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    academic_year VARCHAR(10) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    description TEXT,
    school_id BIGINT REFERENCES schools(id),
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by VARCHAR(255)
);

CREATE INDEX idx_academic_calendar_school ON academic_calendars(school_id);
CREATE INDEX idx_academic_calendar_year ON academic_calendars(academic_year);
