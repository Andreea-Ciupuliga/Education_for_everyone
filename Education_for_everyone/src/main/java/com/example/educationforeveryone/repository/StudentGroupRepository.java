package com.example.educationforeveryone.repository;

import com.example.educationforeveryone.dtos.GetStudentDto;
import com.example.educationforeveryone.models.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Long> {
    @Query("SELECT g from StudentGroup g where g.student.id =:studentId AND g.group.id =:groupId")
    Optional<StudentGroup> findByStudentIdAndGroupId(@Param("studentId") Long studentId, @Param("groupId") Long groupId);

    @Query("SELECT new com.example.educationforeveryone.dtos.GetStudentDto( g.studentFirstName, g.studentLastName, s.email) FROM StudentGroup g JOIN Student s ON g.student.id = s.id where g.group.id=:groupId")
    List<GetStudentDto> findAllStudentsByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT  g.group.id FROM StudentGroup g WHERE g.student.id=:studentId")
    List<Long> findGroupIdsByStudentId(@Param("studentId") Long studentId);

}
