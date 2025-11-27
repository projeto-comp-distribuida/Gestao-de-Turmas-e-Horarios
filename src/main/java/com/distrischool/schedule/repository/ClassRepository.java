package com.distrischool.schedule.repository;

import com.distrischool.schedule.entity.Class;
import com.distrischool.schedule.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByCode(String code);
    List<Class> findBySchool(School school);
    List<Class> findBySchoolAndAcademicYear(School school, String academicYear);
    List<Class> findBySchoolAndActiveTrue(School school);
    
    /**
     * Busca classes que usam uma sala específica e estão ativas
     */
    List<Class> findByRoomAndActiveTrue(String room);
    
    /**
     * Verifica conflitos de sala entre classes através de seus schedules
     * Duas classes não podem usar a mesma sala no mesmo horário
     * Carrega relacionamentos para evitar LazyInitializationException
     */
    @Query("SELECT DISTINCT c FROM Class c " +
           "LEFT JOIN FETCH c.school " +
           "LEFT JOIN FETCH c.shift " +
           "LEFT JOIN FETCH c.subject " +
           "LEFT JOIN FETCH c.schedules s " +
           "LEFT JOIN FETCH s.subject " +
           "LEFT JOIN FETCH c.students " +
           "LEFT JOIN FETCH c.teachers " +
           "WHERE c.room = :room " +
           "AND EXISTS (SELECT 1 FROM Schedule s2 WHERE s2.classEntity.id = c.id " +
           "            AND s2.dayOfWeek = :dayOfWeek " +
           "            AND s2.startTime < :endTime " +
           "            AND s2.endTime > :startTime " +
           "            AND s2.active = true) " +
           "AND c.active = true " +
           "AND c.id != :excludeClassId")
    List<Class> findRoomConflicts(
        @Param("room") String room,
        @Param("dayOfWeek") java.time.DayOfWeek dayOfWeek,
        @Param("startTime") java.time.LocalTime startTime,
        @Param("endTime") java.time.LocalTime endTime,
        @Param("excludeClassId") Long excludeClassId
    );
    
    /**
     * Busca todas as classes com relacionamentos carregados (eager fetch)
     * para evitar LazyInitializationException
     * 
     * Carrega as entidades ManyToOne (school, shift, subject) e as coleções
     * (schedules, students, teachers). Usa DISTINCT para evitar duplicatas
     * causadas por múltiplos JOINs com coleções.
     * 
     * IMPORTANTE: O subject é carregado explicitamente para garantir que
     * sempre esteja disponível, mesmo quando há problemas com múltiplas coleções.
     */
    @Query("SELECT DISTINCT c FROM Class c " +
           "LEFT JOIN FETCH c.school " +
           "LEFT JOIN FETCH c.shift " +
           "LEFT JOIN FETCH c.subject " +
           "LEFT JOIN FETCH c.schedules s " +
           "LEFT JOIN FETCH s.subject ss " +
           "LEFT JOIN FETCH c.students " +
           "LEFT JOIN FETCH c.teachers " +
           "WHERE c.active = true AND c.deletedAt IS NULL " +
           "ORDER BY c.id")
    List<Class> findAllWithRelations();
    
    /**
     * Busca uma classe por ID com relacionamentos carregados (eager fetch)
     * para evitar LazyInitializationException
     */
    @Query("SELECT DISTINCT c FROM Class c " +
           "LEFT JOIN FETCH c.school " +
           "LEFT JOIN FETCH c.shift " +
           "LEFT JOIN FETCH c.subject " +
           "LEFT JOIN FETCH c.schedules s " +
           "LEFT JOIN FETCH s.subject " +
           "LEFT JOIN FETCH c.students " +
           "LEFT JOIN FETCH c.teachers " +
           "WHERE c.id = :id AND c.active = true AND c.deletedAt IS NULL")
    Optional<Class> findByIdWithRelations(@Param("id") Long id);
}
