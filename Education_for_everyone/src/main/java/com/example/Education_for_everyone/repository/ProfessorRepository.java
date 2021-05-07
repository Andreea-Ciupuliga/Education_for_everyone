package com.example.Education_for_everyone.repository;

import com.example.Education_for_everyone.models.Professor;
import com.example.Education_for_everyone.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor,Long> {
    Optional<Professor> findByUsername(String name);
}
