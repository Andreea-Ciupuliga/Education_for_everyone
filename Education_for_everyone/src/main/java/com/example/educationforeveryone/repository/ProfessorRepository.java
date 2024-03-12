package com.example.educationforeveryone.repository;

import com.example.educationforeveryone.dtos.GetProfessorDto;
import com.example.educationforeveryone.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByUsername(String name);

    Optional<Professor> findByEmail(String email);

    @Query("SELECT new com.example.educationforeveryone.dtos.GetProfessorDto(p.firstName,p.lastName,p.email) FROM Professor p")
    List<GetProfessorDto> findAllProfessors();

    @Query("SELECT new com.example.educationforeveryone.dtos.GetProfessorDto(p.firstName,p.lastName,p.email) FROM Professor p WHERE p.lastName LIKE %:name% OR p.firstName LIKE %:name%")
    List<GetProfessorDto> findAllProfessorsByName(@Param("name") String name);
}
