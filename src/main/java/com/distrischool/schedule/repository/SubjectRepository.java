package com.distrischool.schedule.repository;

import com.distrischool.schedule.entity.School;
import com.distrischool.schedule.entity.Subject;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByCode(String code);
    List<Subject> findBySchool(School school);
    List<Subject> findBySchoolAndActiveTrue(School school);
    
    @EntityGraph(attributePaths = {"academicCenter"})
    @Override
    List<Subject> findAll();
    
    @EntityGraph(attributePaths = {"academicCenter"})
    @Override
    Optional<Subject> findById(Long id);
}
