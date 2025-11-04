package com.distrischool.schedule.repository;

import com.distrischool.schedule.entity.Holiday;
import com.distrischool.schedule.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {
    List<Holiday> findBySchool(School school);
    List<Holiday> findByDate(LocalDate date);
    List<Holiday> findBySchoolAndDate(School school, LocalDate date);
    List<Holiday> findBySchoolAndActiveTrue(School school);
}
