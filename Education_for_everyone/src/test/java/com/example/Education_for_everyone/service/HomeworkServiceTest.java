package com.example.Education_for_everyone.service;
import com.example.Education_for_everyone.exceptions.HomeworkNotFoundException;
import com.example.Education_for_everyone.models.Homework;
import com.example.Education_for_everyone.repository.HomeworkRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class HomeworkServiceTest {

    @Mock//ne permite sa nu folosim o instanta reala de HomeworkRepository ci putem noi sa definim comportametul lui HomeworkRepository
    private HomeworkRepository homeworkRepository;

    @Autowired
    private HomeworkService homeworkService;

    @BeforeEach //avem adnotarea asta care se apeleaza inaintea fiecarui test
    void before() {
        homeworkService = new HomeworkService(homeworkRepository);
    }

    @Test
    void deleteHomeworkShouldSucceed() {
        //Arrange
        Long id=1L;
        String deadline= "deadline";
        Long points = 10L;
        String task = "task";

        Homework homework = Homework.builder().id(id).deadline(deadline).points(points).task(task).build();

        //Act
        when(homeworkRepository.findById(homework.getId())).thenReturn(Optional.of(homework));
        //cand cauta tema dupa id returnez un optional de tema ptc l-a gasit in baza de date

        homeworkService.removeHomework(homework.getId());

        //Assert
        verify(homeworkRepository, times(1)).findById(homework.getId());//verific daca se face interactiunea cu findById doar o data
        verify(homeworkRepository,times(1)).delete(homework);//verific daca delete-ul sa se apeleze o data

    }


    @Test
    void deleteHomeworkShouldFailIfHomeworkIsNotFound() {
        //Arrange
        Long id=1L;
        String deadline= "deadline";
        Long points = 10L;
        String task = "task";

        Homework homework = Homework.builder().id(id).deadline(deadline).points(points).task(task).build();

        //Act
        when(homeworkRepository.findById(homework.getId())).thenReturn(Optional.empty());
        //cand cauta tema dupa id returnez un optional empty ptc nu am gasit o in baza de date


        //Assert
        //eu verific cazul in care tema nu exista asa ca ma astept ca metoda mea sa arunce o exceptie deci rezultatul dorit ar fi sa arunce o exceptie
        Assertions.assertThrows(HomeworkNotFoundException.class, () ->homeworkService.removeHomework(homework.getId()));


        verify(homeworkRepository, times(1)).findById(homework.getId());//verific daca se face interactiunea cu findById doar o data
        verify(homeworkRepository,never()).delete(any(Homework.class));//ma asigur ca nu s-a apelat delete

    }


    @Test
    void getHomeworkShouldSucceed() {
        //Arrange
        Long id=1L;
        String deadline= "deadline";
        Long points = 10L;
        String task = "task";


        Homework homework = Homework.builder().id(id).deadline(deadline).points(points).task(task).build();

        //Act
        when(homeworkRepository.findById(id)).thenReturn(Optional.of(homework)); //definesc un comportament pt id-ul 1
        Homework result=homeworkService.getHomework(id);


        //Assert
        Assertions.assertEquals(deadline,result.getDeadline()); //rezultatul meu trebuie sa aiba deadline egal cu deadline pe care il definesc eu in Arrange
        Assertions.assertEquals(points,result.getPoints());//rezultatul meu trebuie sa aiba points egal cu points pe care il definesc eu in Arrange
        Assertions.assertEquals(task,result.getTask());//rezultatul meu trebuie sa aiba task egal cu task pe care il definesc eu in Arrange
        verify(homeworkRepository).findById(anyLong());
    }
}