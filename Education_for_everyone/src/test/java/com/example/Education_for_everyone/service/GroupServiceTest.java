package com.example.Education_for_everyone.service;
import com.example.Education_for_everyone.dtos.RegisterGroupDto;
import com.example.Education_for_everyone.dtos.RegisterProfessorDto;
import com.example.Education_for_everyone.exceptions.GroupAlreadyExistException;
import com.example.Education_for_everyone.exceptions.UserNotFoundException;
import com.example.Education_for_everyone.models.Group;
import com.example.Education_for_everyone.models.Professor;
import com.example.Education_for_everyone.repository.GroupRepository;
import com.example.Education_for_everyone.repository.ProfessorRepository;
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
class GroupServiceTest {
    @Mock
    private GroupRepository groupRepository;
    @Mock//ne permite sa nu folosim o instanta reala de ProfessorRepository ci putem noi sa definim comportametul lui ProfessorRepository
    private ProfessorRepository professorRepository;
    @Autowired
    GroupService groupService;

    @BeforeEach //avem adnotarea asta care se apeleaza inaintea fiecarui test
    void before() {
        groupService = new GroupService(groupRepository,professorRepository);
    }

    @Test//daca exista un grup cu acelasi nume in baza de date sa se arunce o exceptie
    void registerShouldFailIfGroupNameIsAlreadyTaken() {

        //Arrange

        String groupName = "group1";
        String professorUsername="prof";
        Group group=Group.builder().groupName(groupName).build();
        RegisterGroupDto registerGroupDto=RegisterGroupDto.builder().groupName(groupName).build();

        //Act

        when(groupRepository.findByGroupName(groupName)).thenReturn(Optional.of(group)); /* definesc ce se intampla cand se apeleaza groupRepository.findByGroupName
        si anume o sa returnez un optional de group ptc asta returneaza findByGroupName. Astfel if-ul din GroupService va fi true si se va arunca o exceptie*/


        //Assert

        //eu verific cazul in care grupul exista cu numele ala asa ca ma astept ca metoda mea sa arunce o exceptie deci rezultatul dorit ar fi sa arunce o exceptie
        Assertions.assertThrows(GroupAlreadyExistException.class, () -> groupService.registerGroup(registerGroupDto,professorUsername));


        //verific daca se face interactiunea cu findByGroupName doar o data
        verify(groupRepository, times(1)).findByGroupName(groupName);

        //vreau sa ma asigur ca groupRepository.save(group) nu s-a apelat (ca nu s-a fct save) si ca flow-ul s-a intrerupt dupa ce am aruncat exceptia
        verify(groupRepository, never()).save(any(Group.class));

    }

    @Test//daca exista un grup cu acelasi nume in baza de date sa se arunce o exceptie
    void registerShouldFailIfProfessorDoesNotExist() {

        //Arrange

        String groupName = "group1";
        String professorUsername="prof";
        RegisterGroupDto registerGroupDto=RegisterGroupDto.builder().groupName(groupName).build();

        //Act

        when(professorRepository.findByUsername(professorUsername)).thenReturn(Optional.empty()); /* definesc ce se intampla cand se apeleaza professorRepository.findByUsername(professorUsername)
        si anume o sa returnez un optional empty adica sa nu se gaseasca un profesor. Astfel if-ul din GroupService va fi true si se va arunca o exceptie*/


        //Assert

        //eu verific cazul in care profesorul nu exista asa ca ma astept ca metoda mea sa arunce o exceptie deci rezultatul dorit ar fi sa arunce o exceptie
        Assertions.assertThrows(UserNotFoundException.class, () -> groupService.registerGroup(registerGroupDto,professorUsername));


        //verific daca se face interactiunea cu findByUsername doar o data
        verify(professorRepository, times(1)).findByUsername(professorUsername);

        //vreau sa ma asigur ca groupRepository.save(group) nu s-a apelat (ca nu s-a fct save) si ca flow-ul s-a intrerupt dupa ce am aruncat exceptia
        verify(groupRepository, never()).save(any(Group.class));

    }

    @Test //in testul asta verificam ca registerul se face cu succes
    void registerShouldSucceed() {

        //Arrange

        String groupName = "group1";
        String professorUsername="prof";
        RegisterGroupDto registerGroupDto=RegisterGroupDto.builder().groupName(groupName).build();
        Professor professor=Professor.builder().username(professorUsername).build();

        //Act
        when(groupRepository.findByGroupName(groupName)).thenReturn(Optional.empty());
        when(professorRepository.findByUsername(professorUsername)).thenReturn(Optional.of(professor));
        /* definesc ce se intampla cand se apeleaza groupRepository.findByGroupName si anume
        returnez un optional empty ca sa nu mai fie prezent un grup cu acelasi nume si asa nu s-ar mai arunca exceptia din if SI
        definesc ce se intampla cand se apeleaza professorRepository.findByUsername si anume returnez un optional de profesor ca sa
        se gaseasca profesorul respectiv si sa nu arunce exceptie cu user not found */

        groupService.registerGroup(registerGroupDto,professorUsername);



        //Assert
        verify(groupRepository, times(1)).findByGroupName(groupName);//verific daca se face interactiunea cu findByGroupName(groupName) doar o data
        verify(groupRepository, times(1)).save(any(Group.class));//verific daca save-ul sa se apeleze o datea
    }



}