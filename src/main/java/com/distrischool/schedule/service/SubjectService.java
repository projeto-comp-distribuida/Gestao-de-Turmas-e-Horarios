package com.distrischool.schedule.service;

import com.distrischool.schedule.dto.SubjectRequestDTO;
import com.distrischool.schedule.entity.AcademicCenter;
import com.distrischool.schedule.entity.School;
import com.distrischool.schedule.entity.Subject;
import com.distrischool.schedule.exception.BusinessException;
import com.distrischool.schedule.exception.ResourceNotFoundException;
import com.distrischool.schedule.repository.AcademicCenterRepository;
import com.distrischool.schedule.repository.SchoolRepository;
import com.distrischool.schedule.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Serviço para gerenciar cursos (subjects).
 */
@Service
@RequiredArgsConstructor
public class SubjectService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SubjectService.class);
    
    private final SubjectRepository subjectRepository;
    private final SchoolRepository schoolRepository;
    private final AcademicCenterRepository academicCenterRepository;

    @Transactional
    public Subject create(SubjectRequestDTO dto) {
        log.info("Criando curso: {}", dto.getName());

        // Obter a escola padrão (única escola do sistema)
        School school = getDefaultSchool();

        // Validar centro acadêmico se fornecido
        AcademicCenter academicCenter = null;
        if (dto.getAcademicCenterId() != null) {
            academicCenter = academicCenterRepository.findById(dto.getAcademicCenterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Centro Acadêmico", dto.getAcademicCenterId()));
        }

        // Verificar se código já existe
        if (dto.getCode() != null && !dto.getCode().isEmpty()) {
            subjectRepository.findByCode(dto.getCode())
                    .ifPresent(s -> {
                        throw new BusinessException("Já existe um curso com o código: " + dto.getCode());
                    });
        }

        // Criar curso
        Subject subject = Subject.builder()
                .name(dto.getName())
                .code(dto.getCode())
                .workloadHours(dto.getWorkloadHours())
                .description(dto.getDescription())
                .school(school)
                .academicCenter(academicCenter)
                .active(true)
                .build();

        Subject saved = subjectRepository.save(subject);
        log.info("Curso criado com sucesso: {}", saved.getId());
        return saved;
    }

    @Transactional
    public Subject update(Long id, SubjectRequestDTO dto) {
        log.info("Atualizando curso: {}", id);

        Subject existing = subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", id));

        // Validar centro acadêmico se fornecido
        AcademicCenter academicCenter = null;
        if (dto.getAcademicCenterId() != null) {
            academicCenter = academicCenterRepository.findById(dto.getAcademicCenterId())
                    .orElseThrow(() -> new ResourceNotFoundException("Centro Acadêmico", dto.getAcademicCenterId()));
        }

        // Verificar se código já existe (exceto para o próprio curso)
        if (dto.getCode() != null && !dto.getCode().isEmpty() && !dto.getCode().equals(existing.getCode())) {
            subjectRepository.findByCode(dto.getCode())
                    .ifPresent(s -> {
                        throw new BusinessException("Já existe um curso com o código: " + dto.getCode());
                    });
        }

        // Atualizar campos
        existing.setName(dto.getName());
        existing.setCode(dto.getCode());
        existing.setWorkloadHours(dto.getWorkloadHours());
        existing.setDescription(dto.getDescription());
        existing.setAcademicCenter(academicCenter);

        Subject saved = subjectRepository.save(existing);
        log.info("Curso atualizado com sucesso: {}", saved.getId());
        return saved;
    }

    public Subject findById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso", id));
    }

    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    public List<Subject> findByAcademicCenter(Long academicCenterId) {
        // Validar que o centro acadêmico existe
        academicCenterRepository.findById(academicCenterId)
                .orElseThrow(() -> new ResourceNotFoundException("Centro Acadêmico", academicCenterId));
        
        return subjectRepository.findAll().stream()
                .filter(s -> s.getAcademicCenter() != null && s.getAcademicCenter().getId().equals(academicCenterId))
                .toList();
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

