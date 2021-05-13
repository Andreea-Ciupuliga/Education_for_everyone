package com.example.Education_for_everyone.repository;

import com.example.Education_for_everyone.models.Homework;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeworkRepository extends JpaRepository<Homework,Long> {

}
