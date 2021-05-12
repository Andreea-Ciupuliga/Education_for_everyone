package com.example.Education_for_everyone.repository;

import com.example.Education_for_everyone.dtos.GetBookDto;
import com.example.Education_for_everyone.dtos.GetProfessorDto;
import com.example.Education_for_everyone.models.Professor;
import com.example.Education_for_everyone.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor,Long> {
    Optional<Professor> findByUsername(String name);

    @Query("SELECT new com.example.Education_for_everyone.dtos.GetProfessorDto(p.firstName,p.lastName,p.email) FROM Professor p")
    List<GetProfessorDto> findAllProfessors();

    @Query("SELECT new com.example.Education_for_everyone.dtos.GetProfessorDto(p.firstName,p.lastName,p.email) FROM Professor p WHERE p.lastName LIKE %:name% OR p.firstName LIKE %:name%")
    List<GetProfessorDto> findAllProfessorsByName(@Param("name")String name);

}
