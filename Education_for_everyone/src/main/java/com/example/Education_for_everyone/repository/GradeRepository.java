package com.example.Education_for_everyone.repository;

import com.example.Education_for_everyone.models.Grade;
import com.example.Education_for_everyone.models.StudentsBorrowBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeRepository  extends JpaRepository<Grade,Long> {

    @Query("SELECT g from Grade g where g.student.id =:studentId AND g.homework.id =:homeworkId")
    Optional<Grade> findBystudentIdAndhomeworkId(@Param("studentId") Long studentId, @Param("homeworkId")Long homeworkId);

}
