package com.distrischool.schedule.repository;

import com.distrischool.schedule.entity.Class;
import com.distrischool.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByClassEntity(Class classEntity);
    List<Schedule> findByClassEntityAndActiveTrue(Class classEntity);
    
    @Query("SELECT s FROM Schedule s WHERE s.room = :room AND s.dayOfWeek = :dayOfWeek " +
           "AND s.startTime < :endTime AND s.endTime > :startTime AND s.active = true " +
           "AND s.id != :excludeId")
    List<Schedule> findRoomConflicts(@Param("room") String room,
                                     @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                     @Param("startTime") LocalTime startTime,
                                     @Param("endTime") LocalTime endTime,
                                     @Param("excludeId") Long excludeId);
    
    @Query("SELECT s FROM Schedule s WHERE s.teacherId = :teacherId AND s.dayOfWeek = :dayOfWeek " +
           "AND s.startTime < :endTime AND s.endTime > :startTime AND s.active = true " +
           "AND s.id != :excludeId")
    List<Schedule> findTeacherConflicts(@Param("teacherId") Long teacherId,
                                        @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                        @Param("startTime") LocalTime startTime,
                                        @Param("endTime") LocalTime endTime,
                                        @Param("excludeId") Long excludeId);
    
    @Query("SELECT s FROM Schedule s WHERE s.classEntity.id = :classId AND s.dayOfWeek = :dayOfWeek " +
           "AND s.startTime < :endTime AND s.endTime > :startTime AND s.active = true " +
           "AND s.id != :excludeId")
    List<Schedule> findClassConflicts(@Param("classId") Long classId,
                                      @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                      @Param("startTime") LocalTime startTime,
                                      @Param("endTime") LocalTime endTime,
                                      @Param("excludeId") Long excludeId);
    
    /**
     * Busca um schedule por ID com relacionamentos carregados (eager fetch)
     * para evitar LazyInitializationException
     */
    @Query("SELECT DISTINCT s FROM Schedule s " +
           "LEFT JOIN FETCH s.classEntity " +
           "LEFT JOIN FETCH s.subject " +
           "LEFT JOIN FETCH s.shift " +
           "WHERE s.id = :id")
    java.util.Optional<Schedule> findByIdWithRelations(@Param("id") Long id);
    
    /**
     * Busca todos os schedules com relacionamentos carregados (eager fetch)
     * para evitar LazyInitializationException
     */
    @Query("SELECT DISTINCT s FROM Schedule s " +
           "LEFT JOIN FETCH s.classEntity " +
           "LEFT JOIN FETCH s.subject " +
           "LEFT JOIN FETCH s.shift")
    List<Schedule> findAllWithRelations();
}
