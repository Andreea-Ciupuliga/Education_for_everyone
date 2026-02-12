package com.example.educationforeveryone.repository;

import com.example.educationforeveryone.models.StudentHomework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentHomeworkRepository extends JpaRepository<StudentHomework, Long> {
    @Query("SELECT g from StudentHomework g where g.student.id =:studentId AND g.homework.id =:homeworkId")
    Optional<StudentHomework> findByStudentIdAndHomeworkId(@Param("studentId") Long studentId, @Param("homeworkId") Long homeworkId);

    @Query("SELECT g.score from StudentHomework g where g.student.id =:studentId")
    List<Long> findScoreByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT g.homework.id, g.homework.task, g.score from StudentHomework g where g.student.id =:studentId")
    List<String> showScoreByStudentId(@Param("studentId") Long studentId);

}
