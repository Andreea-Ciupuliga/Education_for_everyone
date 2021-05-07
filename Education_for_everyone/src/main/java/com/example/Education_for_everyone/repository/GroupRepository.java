package com.example.Education_for_everyone.repository;

import com.example.Education_for_everyone.dtos.GetBookDto;
import com.example.Education_for_everyone.dtos.GetGroupDto;
import com.example.Education_for_everyone.models.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group,Long> {

    Optional<Group> findByGroupName(String name);


    //List<Group> findAllBySubjectContains(String name);
    @Query("SELECT new com.example.Education_for_everyone.dtos.GetGroupDto(g.groupName, g.subject, g.yearOfStudy, g.availableSeats , p.lastName, p.firstName) FROM Group g join Professor p  on g.professor.id = p.id WHERE g.subject LIKE %:subject%")
    List<GetGroupDto> findAllBySubjectContains(@Param("subject")String subject);

}
