package com.example.Education_for_everyone.repository;

import com.example.Education_for_everyone.models.GroupOfStudents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupOfStudentsRepository extends JpaRepository<GroupOfStudents, Long> {


    @Query("SELECT g from GroupOfStudents g where g.student.id =:studentId AND g.group.id =:groupId")
    Optional<GroupOfStudents> findBystudentIdAndgroupId(@Param("studentId") Long studentId, @Param("groupId")Long groupId);

}
