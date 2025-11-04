package com.distrischool.schedule.repository;

import com.distrischool.schedule.entity.Class;
import com.distrischool.schedule.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByCode(String code);
    List<Class> findBySchool(School school);
    List<Class> findBySchoolAndAcademicYear(School school, String academicYear);
    List<Class> findBySchoolAndActiveTrue(School school);
}
