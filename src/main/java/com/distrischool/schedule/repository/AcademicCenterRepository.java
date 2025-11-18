package com.distrischool.schedule.repository;

import com.distrischool.schedule.entity.AcademicCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AcademicCenterRepository extends JpaRepository<AcademicCenter, Long> {
    Optional<AcademicCenter> findByCode(String code);
    List<AcademicCenter> findByActiveTrue();
}




