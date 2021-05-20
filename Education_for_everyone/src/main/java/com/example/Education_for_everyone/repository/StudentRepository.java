package com.example.Education_for_everyone.repository;

import com.example.Education_for_everyone.dtos.GetProfessorDto;
import com.example.Education_for_everyone.dtos.GetStudentDto;
import com.example.Education_for_everyone.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {
    Optional<Student> findByUsername(String name);

    Optional<Student> findByEmail(String email);

    @Query("SELECT new com.example.Education_for_everyone.dtos.GetStudentDto(s.firstName,s.lastName,s.email) FROM Student s")
    List<GetStudentDto> findAllStudents();

    @Query("SELECT new com.example.Education_for_everyone.dtos.GetStudentDto(s.firstName,s.lastName,s.email) FROM Student s WHERE s.lastName LIKE %:name% OR s.firstName LIKE %:name%")
    List<GetStudentDto> findAllStudentsByName(@Param("name")String name);
}
