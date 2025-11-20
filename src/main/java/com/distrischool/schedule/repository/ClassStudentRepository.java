package com.distrischool.schedule.repository;

import com.distrischool.schedule.entity.ClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassStudentRepository extends JpaRepository<ClassStudent, Long> {
    @Query("SELECT cs FROM ClassStudent cs WHERE cs.classEntity.id = :classId")
    List<ClassStudent> findByClassId(@Param("classId") Long classId);
    
    @Query("SELECT cs FROM ClassStudent cs WHERE cs.classEntity.id = :classId AND cs.studentId = :studentId")
    Optional<ClassStudent> findByClassIdAndStudentId(@Param("classId") Long classId, @Param("studentId") Long studentId);
    
    @Query("SELECT COUNT(cs) > 0 FROM ClassStudent cs WHERE cs.classEntity.id = :classId AND cs.studentId = :studentId")
    boolean existsByClassIdAndStudentId(@Param("classId") Long classId, @Param("studentId") Long studentId);
    
    /**
     * Verifica se um aluno já está em outra turma para o mesmo curso/disciplina
     */
    @Query("SELECT cs FROM ClassStudent cs " +
           "JOIN cs.classEntity c " +
           "JOIN c.schedules s " +
           "WHERE cs.studentId = :studentId " +
           "AND s.subject.id = :subjectId " +
           "AND c.id != :excludeClassId " +
           "AND c.active = true")
    List<ClassStudent> findByStudentIdAndSubjectId(
        @Param("studentId") Long studentId,
        @Param("subjectId") Long subjectId,
        @Param("excludeClassId") Long excludeClassId
    );
    
    /**
     * Verifica se um aluno já está em outra turma para o mesmo curso/disciplina (sem excluir nenhuma turma)
     */
    @Query("SELECT cs FROM ClassStudent cs " +
           "JOIN cs.classEntity c " +
           "JOIN c.schedules s " +
           "WHERE cs.studentId = :studentId " +
           "AND s.subject.id = :subjectId " +
           "AND c.active = true")
    List<ClassStudent> findByStudentIdAndSubjectIdAll(
        @Param("studentId") Long studentId,
        @Param("subjectId") Long subjectId
    );
}


