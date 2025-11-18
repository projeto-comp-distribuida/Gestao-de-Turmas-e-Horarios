package com.distrischool.schedule.service;

import com.distrischool.schedule.dto.ApiResponse;
import com.distrischool.schedule.dto.ClassRequestDTO;
import com.distrischool.schedule.entity.Class;
import com.distrischool.schedule.entity.ClassStudent;
import com.distrischool.schedule.entity.ClassTeacher;
import com.distrischool.schedule.entity.Schedule;
import com.distrischool.schedule.entity.School;
import com.distrischool.schedule.entity.Shift;
import com.distrischool.schedule.exception.BusinessException;
import com.distrischool.schedule.exception.ResourceNotFoundException;
import com.distrischool.schedule.feign.StudentServiceClient;
import com.distrischool.schedule.feign.TeacherServiceClient;
import com.distrischool.schedule.kafka.ClassEventProducer;
import com.distrischool.schedule.repository.ClassRepository;
import com.distrischool.schedule.repository.ClassStudentRepository;
import com.distrischool.schedule.repository.ClassTeacherRepository;
import com.distrischool.schedule.repository.ScheduleRepository;
import com.distrischool.schedule.repository.SchoolRepository;
import com.distrischool.schedule.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Serviço para gerenciar turmas (classes).
 */
@Service
@RequiredArgsConstructor
public class ClassService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ClassService.class);
    
    private final ClassRepository classRepository;
    private final SchoolRepository schoolRepository;
    private final ShiftRepository shiftRepository;
    private final ClassStudentRepository classStudentRepository;
    private final ClassTeacherRepository classTeacherRepository;
    private final StudentServiceClient studentServiceClient;
    private final TeacherServiceClient teacherServiceClient;
    private final ScheduleRepository scheduleRepository;
    private final ClassEventProducer eventProducer;

    @Transactional
    public Class create(ClassRequestDTO dto) {
        log.info("Criando turma: {}", dto.getName());

        // Obter a escola padrão (única escola do sistema)
        School school = getDefaultSchool();

        // Validar turno se fornecido
        Shift shift = null;
        if (dto.getShiftId() != null) {
            shift = getOrCreateShift(dto.getShiftId());
        }

        // Validar estudantes via Feign
        if (dto.getStudentIds() != null && !dto.getStudentIds().isEmpty()) {
            validateStudents(dto.getStudentIds());
        }

        // Validar professores via Feign
        if (dto.getTeacherIds() != null && !dto.getTeacherIds().isEmpty()) {
            validateTeachers(dto.getTeacherIds());
        }

        // Criar turma
        Class classEntity = Class.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .academicYear(dto.getAcademicYear())
                .period(dto.getPeriod())
                .capacity(dto.getCapacity())
                .school(school)
                .shift(shift)
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .room(dto.getRoom())
                .active(true)
                .currentStudents(0)
                .build();

        Class saved = classRepository.save(classEntity);

        // Adicionar estudantes
        if (dto.getStudentIds() != null && !dto.getStudentIds().isEmpty()) {
            addStudentsInternal(saved.getId(), dto.getStudentIds(), null);
        }

        // Adicionar professores
        if (dto.getTeacherIds() != null && !dto.getTeacherIds().isEmpty()) {
            addTeachersInternal(saved.getId(), dto.getTeacherIds());
        }

        // Atualizar contador de estudantes
        saved.setCurrentStudents(saved.getStudents().size());
        saved = classRepository.save(saved);

        // Publicar evento Kafka
        eventProducer.publishClassCreated(saved.getId(), saved.getSchool().getId(), saved.getName());

        log.info("Turma criada com sucesso: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Class update(Long id, ClassRequestDTO dto) {
        log.info("Atualizando turma: {}", id);

        Class existing = classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma", id));

        // Obter a escola padrão (única escola do sistema)
        School school = getDefaultSchool();

        // Validar turno se fornecido
        Shift shift = null;
        if (dto.getShiftId() != null) {
            shift = getOrCreateShift(dto.getShiftId());
        }

        // Validar estudantes via Feign
        if (dto.getStudentIds() != null && !dto.getStudentIds().isEmpty()) {
            validateStudents(dto.getStudentIds());
        }

        // Validar professores via Feign
        if (dto.getTeacherIds() != null && !dto.getTeacherIds().isEmpty()) {
            validateTeachers(dto.getTeacherIds());
        }

        // Atualizar campos
        existing.setName(dto.getName());
        existing.setCode(dto.getCode());
        existing.setAcademicYear(dto.getAcademicYear());
        existing.setPeriod(dto.getPeriod());
        existing.setCapacity(dto.getCapacity());
        existing.setSchool(school);
        existing.setShift(shift);
        existing.setStartDate(dto.getStartDate());
        existing.setEndDate(dto.getEndDate());
        existing.setRoom(dto.getRoom());

        // Sincronizar estudantes
        if (dto.getStudentIds() != null) {
            syncStudents(existing, dto.getStudentIds());
        }

        // Sincronizar professores
        if (dto.getTeacherIds() != null) {
            syncTeachers(existing, dto.getTeacherIds());
        }

        // Atualizar contador
        existing.setCurrentStudents(existing.getStudents().size());

        Class saved = classRepository.save(existing);
        
        // Publicar evento Kafka
        eventProducer.publishClassUpdated(saved.getId(), saved.getSchool().getId(), saved.getName());

        log.info("Turma atualizada com sucesso: {}", saved.getId());
        return saved;
    }

    @Transactional
    public void addStudents(Long classId, List<Long> studentIds) {
        log.info("Adicionando estudantes à turma: {}", classId);
        addStudentsInternal(classId, studentIds, null);
    }

    private void addStudentsInternal(Long classId, List<Long> studentIds, Long subjectId) {
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma", classId));

        // Validar estudantes
        validateStudents(studentIds);

        // Para cada estudante, verificar se já está em outra turma do mesmo curso
        if (subjectId == null) {
            // Se não temos subjectId, precisamos verificar todos os schedules da turma
            Set<Long> subjectIds = classEntity.getSchedules().stream()
                    .map(s -> s.getSubject().getId())
                    .collect(Collectors.toSet());

            for (Long studentId : studentIds) {
                for (Long subjId : subjectIds) {
                    List<ClassStudent> existing = classStudentRepository.findByStudentIdAndSubjectIdAll(studentId, subjId);
                    if (!existing.isEmpty()) {
                        throw new BusinessException(
                                String.format("Estudante %d já está matriculado em outra turma para o mesmo curso (subjectId: %d)", 
                                        studentId, subjId));
                    }
                }
            }
        } else {
            // Verificar para um subject específico
            for (Long studentId : studentIds) {
                List<ClassStudent> existing = classStudentRepository.findByStudentIdAndSubjectIdAll(studentId, subjectId);
                if (!existing.isEmpty()) {
                    throw new BusinessException(
                            String.format("Estudante %d já está matriculado em outra turma para o mesmo curso (subjectId: %d)", 
                                    studentId, subjectId));
                }
            }
        }

        // Adicionar estudantes
        for (Long studentId : studentIds) {
            if (!classStudentRepository.existsByClassIdAndStudentId(classId, studentId)) {
                ClassStudent classStudent = ClassStudent.builder()
                        .classEntity(classEntity)
                        .studentId(studentId)
                        .build();
                classStudentRepository.save(classStudent);
                
                // Publicar evento Kafka
                eventProducer.publishStudentEnrolled(classId, studentId, classEntity.getSchool().getId());
            }
        }

        // Atualizar contador
        classEntity.setCurrentStudents(classEntity.getStudents().size());
        classRepository.save(classEntity);
    }

    @Transactional
    public void addTeachers(Long classId, List<Long> teacherIds) {
        log.info("Adicionando professores à turma: {}", classId);
        addTeachersInternal(classId, teacherIds);
    }

    private void addTeachersInternal(Long classId, List<Long> teacherIds) {
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma", classId));

        // Validar professores
        validateTeachers(teacherIds);

        // Adicionar professores
        for (Long teacherId : teacherIds) {
            if (!classTeacherRepository.existsByClassIdAndTeacherId(classId, teacherId)) {
                ClassTeacher classTeacher = ClassTeacher.builder()
                        .classEntity(classEntity)
                        .teacherId(teacherId)
                        .build();
                classTeacherRepository.save(classTeacher);
            }
        }
    }

    @Transactional
    public void removeStudent(Long classId, Long studentId) {
        log.info("Removendo estudante {} da turma {}", studentId, classId);
        
        ClassStudent classStudent = classStudentRepository.findByClassIdAndStudentId(classId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Estudante %d não encontrado na turma %d", studentId, classId)));

        classStudentRepository.delete(classStudent);

        // Atualizar contador
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma", classId));
        classEntity.setCurrentStudents(classEntity.getStudents().size());
        classRepository.save(classEntity);
    }

    @Transactional
    public void removeTeacher(Long classId, Long teacherId) {
        log.info("Removendo professor {} da turma {}", teacherId, classId);
        
        ClassTeacher classTeacher = classTeacherRepository.findByClassIdAndTeacherId(classId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Professor %d não encontrado na turma %d", teacherId, classId)));

        classTeacherRepository.delete(classTeacher);
    }

    public Class findById(Long id) {
        return classRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turma", id));
    }

    public List<Class> findAll() {
        return classRepository.findAll();
    }

    public List<Class> findRoomConflicts(Long classId, String room) {
        Class classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Turma", classId));

        if (room == null || room.isEmpty()) {
            return Collections.emptyList();
        }

        // Verificar conflitos através dos schedules da turma
        List<Class> conflicts = new ArrayList<>();
        for (Schedule schedule : classEntity.getSchedules()) {
            List<Class> roomConflicts = classRepository.findRoomConflicts(
                    room,
                    schedule.getDayOfWeek(),
                    schedule.getStartTime(),
                    schedule.getEndTime(),
                    classId
            );
            conflicts.addAll(roomConflicts);
        }

        return conflicts.stream().distinct().collect(Collectors.toList());
    }

    private void validateStudents(List<Long> studentIds) {
        try {
            ApiResponse<List<Map<String, Object>>> response = studentServiceClient.getStudentsByIds(studentIds);
            if (!response.isSuccess() || response.getData() == null || response.getData().size() != studentIds.size()) {
                throw new BusinessException("Um ou mais estudantes não foram encontrados");
            }
        } catch (Exception e) {
            log.error("Erro ao validar estudantes: {}", e.getMessage());
            throw new BusinessException("Erro ao validar estudantes: " + e.getMessage());
        }
    }

    private void validateTeachers(List<Long> teacherIds) {
        try {
            ApiResponse<List<Map<String, Object>>> response = teacherServiceClient.getTeachersByIds(teacherIds);
            if (!response.isSuccess() || response.getData() == null || response.getData().size() != teacherIds.size()) {
                throw new BusinessException("Um ou mais professores não foram encontrados");
            }
        } catch (Exception e) {
            log.error("Erro ao validar professores: {}", e.getMessage());
            throw new BusinessException("Erro ao validar professores: " + e.getMessage());
        }
    }

    private void syncStudents(Class classEntity, List<Long> newStudentIds) {
        Set<Long> currentStudentIds = classEntity.getStudents().stream()
                .map(ClassStudent::getStudentId)
                .collect(Collectors.toSet());

        Set<Long> newStudentIdsSet = new HashSet<>(newStudentIds);

        // Remover estudantes que não estão mais na lista
        List<ClassStudent> toRemove = classEntity.getStudents().stream()
                .filter(cs -> !newStudentIdsSet.contains(cs.getStudentId()))
                .collect(Collectors.toList());
        classStudentRepository.deleteAll(toRemove);

        // Adicionar novos estudantes
        List<Long> toAdd = newStudentIds.stream()
                .filter(id -> !currentStudentIds.contains(id))
                .collect(Collectors.toList());
        
        if (!toAdd.isEmpty()) {
            addStudentsInternal(classEntity.getId(), toAdd, null);
        }
    }

    private void syncTeachers(Class classEntity, List<Long> newTeacherIds) {
        Set<Long> currentTeacherIds = classEntity.getTeachers().stream()
                .map(ClassTeacher::getTeacherId)
                .collect(Collectors.toSet());

        Set<Long> newTeacherIdsSet = new HashSet<>(newTeacherIds);

        // Remover professores que não estão mais na lista
        List<ClassTeacher> toRemove = classEntity.getTeachers().stream()
                .filter(ct -> !newTeacherIdsSet.contains(ct.getTeacherId()))
                .collect(Collectors.toList());
        classTeacherRepository.deleteAll(toRemove);

        // Adicionar novos professores
        List<Long> toAdd = newTeacherIds.stream()
                .filter(id -> !currentTeacherIds.contains(id))
                .collect(Collectors.toList());
        
        if (!toAdd.isEmpty()) {
            addTeachersInternal(classEntity.getId(), toAdd);
        }
    }

    /**
     * Obtém ou cria um turno. Se o turno solicitado não existir e não houver turnos no banco,
     * cria um turno padrão.
     */
    private Shift getOrCreateShift(Long shiftId) {
        return shiftRepository.findById(shiftId)
                .orElseGet(() -> {
                    // Se o turno solicitado não existe e não há turnos no banco, cria um padrão
                    if (shiftRepository.count() == 0) {
                        log.info("Nenhum turno encontrado. Criando turno padrão...");
                        Shift defaultShift = Shift.builder()
                                .name("Manhã")
                                .description("Turno da Manhã")
                                .startTime(java.time.LocalTime.of(7, 0))
                                .endTime(java.time.LocalTime.of(12, 0))
                                .active(true)
                                .build();
                        Shift saved = shiftRepository.save(defaultShift);
                        log.info("Turno padrão criado com sucesso: ID={}, Nome={}", saved.getId(), saved.getName());
                        return saved;
                    }
                    throw new ResourceNotFoundException("Turno", shiftId);
                });
    }

    /**
     * Obtém a escola padrão do sistema (única escola).
     * Se não existir uma escola ativa, cria uma escola padrão automaticamente.
     */
    private School getDefaultSchool() {
        return schoolRepository.findAll().stream()
                .filter(s -> s.getActive() != null && s.getActive())
                .findFirst()
                .orElseGet(() -> {
                    log.info("Nenhuma escola ativa encontrada. Criando escola padrão...");
                    try {
                        School defaultSchool = School.builder()
                                .name("Escola Padrão")
                                .code("ESCOLA-001")
                                .active(true)
                                .build();
                        School saved = schoolRepository.save(defaultSchool);
                        log.info("Escola padrão criada com sucesso: ID={}, Nome={}", saved.getId(), saved.getName());
                        return saved;
                    } catch (Exception e) {
                        // Se houver erro (ex: constraint violation em caso de race condition), tenta buscar novamente
                        log.warn("Erro ao criar escola padrão (pode ser race condition): {}. Tentando buscar novamente...", e.getMessage());
                        return schoolRepository.findAll().stream()
                                .filter(s -> s.getActive() != null && s.getActive())
                                .findFirst()
                                .orElseThrow(() -> new BusinessException("Não foi possível obter ou criar a escola padrão"));
                    }
                });
    }
}

