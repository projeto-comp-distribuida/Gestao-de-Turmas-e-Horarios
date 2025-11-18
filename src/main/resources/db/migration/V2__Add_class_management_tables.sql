-- Migração V2: Adicionar gestão de turmas, centros acadêmicos e presenças
-- V2__Add_class_management_tables.sql

-- Tabela de Centros Acadêmicos
CREATE TABLE IF NOT EXISTS academic_centers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by VARCHAR(255)
);

CREATE INDEX idx_academic_center_code ON academic_centers(code);
CREATE INDEX idx_academic_center_active ON academic_centers(active);

-- Inserir centros acadêmicos pré-definidos
INSERT INTO academic_centers (name, code, description, active, created_at, updated_at) VALUES
('Center of Technology', 'COT', 'Centro de Tecnologia', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Center of Biological Sciences', 'COB', 'Centro de Ciências Biológicas', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Center of Humanities', 'COH', 'Centro de Humanidades', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Center of Exact Sciences', 'COE', 'Centro de Ciências Exatas', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Center of Health Sciences', 'COS', 'Centro de Ciências da Saúde', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Center of Applied Social Sciences', 'COA', 'Centro de Ciências Sociais Aplicadas', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Adicionar academic_center_id à tabela subjects
ALTER TABLE subjects ADD COLUMN IF NOT EXISTS academic_center_id BIGINT REFERENCES academic_centers(id);
CREATE INDEX idx_subject_academic_center ON subjects(academic_center_id);

-- Tabela de relacionamento Class-Student (junction table)
CREATE TABLE IF NOT EXISTS class_students (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    student_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    UNIQUE(class_id, student_id)
);

CREATE INDEX idx_class_student_class ON class_students(class_id);
CREATE INDEX idx_class_student_student ON class_students(student_id);

-- Tabela de relacionamento Class-Teacher (junction table)
CREATE TABLE IF NOT EXISTS class_teachers (
    id BIGSERIAL PRIMARY KEY,
    class_id BIGINT NOT NULL REFERENCES classes(id) ON DELETE CASCADE,
    teacher_id BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    UNIQUE(class_id, teacher_id)
);

CREATE INDEX idx_class_teacher_class ON class_teachers(class_id);
CREATE INDEX idx_class_teacher_teacher ON class_teachers(teacher_id);

-- Tabela de Presenças (Attendance)
CREATE TABLE IF NOT EXISTS attendance (
    id BIGSERIAL PRIMARY KEY,
    schedule_id BIGINT NOT NULL REFERENCES schedules(id) ON DELETE CASCADE,
    student_id BIGINT NOT NULL,
    date DATE NOT NULL,
    present BOOLEAN NOT NULL DEFAULT true,
    marked_by VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    UNIQUE(schedule_id, student_id, date)
);

CREATE INDEX idx_attendance_schedule ON attendance(schedule_id);
CREATE INDEX idx_attendance_student ON attendance(student_id);
CREATE INDEX idx_attendance_date ON attendance(date);
CREATE INDEX idx_attendance_schedule_date ON attendance(schedule_id, date);

-- Adicionar coluna room à tabela classes
ALTER TABLE classes ADD COLUMN IF NOT EXISTS room VARCHAR(100);
CREATE INDEX idx_class_room ON classes(room);

-- Remover colunas antigas de student_ids e teacher_ids (serão substituídas pelas junction tables)
-- Nota: Não removemos as colunas existentes para evitar perda de dados, mas elas não serão mais usadas
-- ALTER TABLE classes DROP COLUMN IF EXISTS student_ids;
-- ALTER TABLE classes DROP COLUMN IF EXISTS teacher_ids;




