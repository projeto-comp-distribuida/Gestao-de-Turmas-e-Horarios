package com.distrischool.schedule.repository;

import com.distrischool.schedule.entity.ClassTeacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassTeacherRepository extends JpaRepository<ClassTeacher, Long> {
    @Query("SELECT ct FROM ClassTeacher ct WHERE ct.classEntity.id = :classId")
    List<ClassTeacher> findByClassId(@Param("classId") Long classId);
    
    @Query("SELECT ct FROM ClassTeacher ct WHERE ct.classEntity.id = :classId AND ct.teacherId = :teacherId")
    Optional<ClassTeacher> findByClassIdAndTeacherId(@Param("classId") Long classId, @Param("teacherId") Long teacherId);
    
    @Query("SELECT COUNT(ct) > 0 FROM ClassTeacher ct WHERE ct.classEntity.id = :classId AND ct.teacherId = :teacherId")
    boolean existsByClassIdAndTeacherId(@Param("classId") Long classId, @Param("teacherId") Long teacherId);
}


