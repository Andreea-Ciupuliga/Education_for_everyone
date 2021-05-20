package com.example.Education_for_everyone.repository;

import com.example.Education_for_everyone.dtos.GetStudentDto;
import com.example.Education_for_everyone.models.GroupOfStudents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupOfStudentsRepository extends JpaRepository<GroupOfStudents, Long> {


    @Query("SELECT g from GroupOfStudents g where g.student.id =:studentId AND g.group.id =:groupId")
    Optional<GroupOfStudents> findBystudentIdAndgroupId(@Param("studentId") Long studentId, @Param("groupId")Long groupId);

    @Query("SELECT new com.example.Education_for_everyone.dtos.GetStudentDto( g.studentFirstName, g.studentLastName, s.email) FROM GroupOfStudents g JOIN Student s ON g.student.id = s.id where g.group.id=:groupId")
    List<GetStudentDto> findAllStudentsByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT  g.group.id FROM GroupOfStudents g WHERE g.student.id=:studentId")
    ArrayList<Long> findGroupIdByStudentId (@Param("studentId") Long studentId);


}
