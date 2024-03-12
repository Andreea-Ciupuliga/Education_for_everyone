package com.example.Education_for_everyone.repository;

import com.example.Education_for_everyone.models.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByStudent_IdAndHomework_Id(@Param("studentId") Long studentId, @Param("homeworkId") Long homeworkId);

    @Query("SELECT g.score from Grade G where g.student.id =:studentId")
    List<Long> findScoreByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT g.homework.id, g.homework.task, g.score from Grade g where g.student.id =:studentId")
    List<String> showScoreByStudentId(@Param("studentId") Long studentId);

}
