package com.example.Education_for_everyone.service;


import com.example.Education_for_everyone.dtos.GetGroupDto;
import com.example.Education_for_everyone.dtos.GetStudentDto;
import com.example.Education_for_everyone.exceptions.*;
import com.example.Education_for_everyone.models.Group;
import com.example.Education_for_everyone.models.Student;
import com.example.Education_for_everyone.models.GroupOfStudents;
import com.example.Education_for_everyone.repository.GroupOfStudentsRepository;
import com.example.Education_for_everyone.repository.GroupRepository;
import com.example.Education_for_everyone.repository.StudentRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupOfStudentsService {

    private StudentRepository studentRepository;
    private GroupRepository groupRepository;
    private GroupOfStudentsRepository groupOfStudentsRepository;


    @Autowired
    public GroupOfStudentsService(StudentRepository studentRepository, GroupRepository groupRepository, GroupOfStudentsRepository groupOfStudentsRepository)
    {
        this.studentRepository = studentRepository;
        this.groupRepository = groupRepository;
        this.groupOfStudentsRepository = groupOfStudentsRepository;
    }



    @SneakyThrows
    public void addStudentInGroup(Long studentId,Long groupId)
    {

        Student student = studentRepository.findById(studentId).orElseThrow(()->new UserNotFoundException("student not found"));
        Group group = groupRepository.findById(groupId).orElseThrow(()->new GroupNotFoundException("group not found"));

        if(groupOfStudentsRepository.findBystudentIdAndgroupId(studentId,groupId).isPresent())
        {
            throw new UserAlreadyExistException("student already in this group");
        }


        if(group.getAvailableSeats()<=0)
        {
            throw new NotEnoughAvailableSeatsException("Not Enough Available Seats");
        }

        GroupOfStudents groupOfStudents=GroupOfStudents.builder()
                .student(Student.builder().id(student.getId()).build())
                .group(Group.builder().id(group.getId()).build())
                .groupName(group.getGroupName())
                .studentFirstName(student.getFirstName())
                .studentLastName(student.getLastName()).build();

        groupOfStudentsRepository.save(groupOfStudents);

        group.setAvailableSeats(group.getAvailableSeats()-1);
        groupRepository.save(group);

    }

    @SneakyThrows
    public void removeStudentFromGroup(Long studentId,Long groupId) {

        if(studentRepository.findById(studentId).isEmpty())
        {
            throw new UserNotFoundException("student not found");
        }

        Group group = groupRepository.findById(groupId).orElseThrow(()->new GroupNotFoundException("group not found"));
        GroupOfStudents groupOfStudents=groupOfStudentsRepository.findBystudentIdAndgroupId(studentId,groupId).orElseThrow(()->new GroupAndStudentDoNotMatchException("Group And Student Do Not Match"));


        groupOfStudentsRepository.delete(groupOfStudents);

        group.setAvailableSeats(group.getAvailableSeats()+1);
        groupRepository.save(group);

    }

    @SneakyThrows
    public List<GetStudentDto> getAllStudentsByGroupId(Long groupId) {

        return groupOfStudentsRepository.findAllStudentsByGroupId(groupId);
    }


}
