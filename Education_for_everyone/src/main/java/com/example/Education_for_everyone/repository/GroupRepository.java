package com.example.Education_for_everyone.repository;

import com.example.Education_for_everyone.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group,Long> {

    Optional<Group> findByGroupName(String name);


    List<Group> findAllBySubjectContains(String name);

}
