package com.example.Education_for_everyone.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.Education_for_everyone.SendEmailService;
import com.example.Education_for_everyone.dtos.RegisterProfessorDto;
import com.example.Education_for_everyone.exceptions.UserAlreadyExistException;
import com.example.Education_for_everyone.exceptions.UserNotFoundException;
import com.example.Education_for_everyone.models.*;
import com.example.Education_for_everyone.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class GradeServiceTest {
    @Mock
    private GradeRepository gradeRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private HomeworkRepository homeworkRepository;
    @Mock
    private SendEmailService sendEmailService;
    @Mock
    private ProfessorRepository professorRepository;
    @Mock
    private GroupOfStudentsRepository groupOfStudentsRepository;
    @Mock
    private GroupRepository groupRepository;


    @Autowired
    GradeService gradeService;

    @BeforeEach //avem adnotarea asta care se apeleaza inaintea fiecarui test
    void before() {
        gradeService = new GradeService(studentRepository,gradeRepository,homeworkRepository,sendEmailService,professorRepository,groupOfStudentsRepository,groupRepository);
    }

    @Test
    void assignHomeworkToStudentShouldFailIfStudentIsNotFound() {

        //Arrange
        Long studentId=1L;
        Long homeworkId=1L;
        String username="username";

        //Act

        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        //Assert

        //eu verific cazul in care studentul nu exista asa ca ma astept ca metoda mea sa arunce o exceptie deci rezultatul dorit ar fi sa arunce o exceptie
        Assertions.assertThrows(UserNotFoundException.class, () -> gradeService.assignHomeworkToStudent(studentId,homeworkId,username));


        //verific daca se face interactiunea cu findById doar o data
        verify(studentRepository, times(1)).findById(studentId);

        //vreau sa ma asigur ca gradeRepository.save(grade) nu s-a apelat (ca nu s-a fct save) si ca flow-ul s-a intrerupt dupa ce am aruncat exceptia
        verify(gradeRepository, never()).save(any(Grade.class));

    }

    @Test
    void assignHomeworkToStudentShouldSucceed() {

        //Arrange
        Long studentId=1L;
        Long homeworkId=1L;
        Long groupId=1L;
        String username="username";
        ArrayList<Long> groupIds = new ArrayList<Long>();
        groupIds.add(groupId);


        Student student=Student.builder().id(studentId).build();
        Professor professor=Professor.builder().username(username).build();
        Homework homework = Homework.builder().id(homeworkId).build();
        Group group=Group.builder().id(groupId).build();

        //Act

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(professorRepository.findByUsername(username)).thenReturn(Optional.of(professor));
        when(homeworkRepository.findById(homeworkId)).thenReturn(Optional.of(homework));
        when(groupOfStudentsRepository.findGroupIdByStudentId(studentId)).thenReturn(groupIds);
        when(groupRepository.findByprofessorIdAndgroupId(professor.getId(),group.getId())).thenReturn(Optional.of(group));

        gradeService.assignHomeworkToStudent(studentId,homeworkId,username);


        //Assert

        //verific daca se face interactiunea cu findById doar o data
        verify(studentRepository, times(1)).findById(studentId);
        verify(professorRepository, times(1)).findByUsername(username);
        verify(homeworkRepository, times(1)).findById(homeworkId);
        verify(groupOfStudentsRepository, times(1)).findGroupIdByStudentId(studentId);
        verify(groupRepository, times(1)).findByprofessorIdAndgroupId(professor.getId(),group.getId());

        //vreau sa ma asigur ca gradeRepository.save(grade) nu s-a apelat (ca nu s-a fct save) si ca flow-ul s-a intrerupt dupa ce am aruncat exceptia
        verify(gradeRepository, times(1)).save(any(Grade.class));

    }

}