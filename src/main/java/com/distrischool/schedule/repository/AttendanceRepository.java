package com.distrischool.schedule.repository;

import com.distrischool.schedule.entity.Attendance;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    @Override
    @EntityGraph(attributePaths = {"schedule", "schedule.classEntity", "schedule.subject"})
    Optional<Attendance> findById(Long id);

    @EntityGraph(attributePaths = {"schedule", "schedule.classEntity", "schedule.subject"})
    List<Attendance> findByScheduleId(Long scheduleId);

    @EntityGraph(attributePaths = {"schedule", "schedule.classEntity", "schedule.subject"})
    List<Attendance> findByScheduleIdAndDate(Long scheduleId, LocalDate date);

    @EntityGraph(attributePaths = {"schedule", "schedule.classEntity", "schedule.subject"})
    List<Attendance> findByStudentIdAndScheduleId(Long studentId, Long scheduleId);

    @EntityGraph(attributePaths = {"schedule", "schedule.classEntity", "schedule.subject"})
    Optional<Attendance> findByScheduleIdAndStudentIdAndDate(Long scheduleId, Long studentId, LocalDate date);
    
    @Query("SELECT a FROM Attendance a " +
           "JOIN FETCH a.schedule s " +
           "JOIN FETCH s.classEntity " +
           "JOIN FETCH s.subject " +
           "WHERE s.id = :scheduleId " +
           "AND a.date = :date")
    List<Attendance> findAttendanceByScheduleAndDate(
        @Param("scheduleId") Long scheduleId,
        @Param("date") LocalDate date
    );
}




