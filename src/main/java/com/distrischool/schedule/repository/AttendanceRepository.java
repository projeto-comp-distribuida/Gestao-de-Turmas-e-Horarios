package com.distrischool.schedule.repository;

import com.distrischool.schedule.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByScheduleId(Long scheduleId);
    List<Attendance> findByScheduleIdAndDate(Long scheduleId, LocalDate date);
    List<Attendance> findByStudentIdAndScheduleId(Long studentId, Long scheduleId);
    Optional<Attendance> findByScheduleIdAndStudentIdAndDate(Long scheduleId, Long studentId, LocalDate date);
    
    @Query("SELECT a FROM Attendance a " +
           "WHERE a.schedule.id = :scheduleId " +
           "AND a.date = :date")
    List<Attendance> findAttendanceByScheduleAndDate(
        @Param("scheduleId") Long scheduleId,
        @Param("date") LocalDate date
    );
}



