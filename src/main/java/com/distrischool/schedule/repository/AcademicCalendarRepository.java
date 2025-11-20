package com.distrischool.schedule.repository;

import com.distrischool.schedule.entity.AcademicCalendar;
import com.distrischool.schedule.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicCalendarRepository extends JpaRepository<AcademicCalendar, Long> {
    Optional<AcademicCalendar> findBySchoolAndAcademicYear(School school, String academicYear);
    List<AcademicCalendar> findBySchool(School school);
    List<AcademicCalendar> findBySchoolAndActiveTrue(School school);
}
