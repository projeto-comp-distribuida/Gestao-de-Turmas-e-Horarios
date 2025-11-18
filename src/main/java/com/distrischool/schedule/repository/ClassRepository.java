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
     */
    @Query("SELECT DISTINCT c FROM Class c " +
           "JOIN c.schedules s " +
           "WHERE c.room = :room " +
           "AND s.dayOfWeek = :dayOfWeek " +
           "AND s.startTime < :endTime " +
           "AND s.endTime > :startTime " +
           "AND c.active = true " +
           "AND s.active = true " +
           "AND c.id != :excludeClassId")
    List<Class> findRoomConflicts(
        @Param("room") String room,
        @Param("dayOfWeek") java.time.DayOfWeek dayOfWeek,
        @Param("startTime") java.time.LocalTime startTime,
        @Param("endTime") java.time.LocalTime endTime,
        @Param("excludeClassId") Long excludeClassId
    );
}
