package com.example.Education_for_everyone.service;
import com.example.Education_for_everyone.SendEmailService;
import com.example.Education_for_everyone.dtos.GetProfessorDto;
import com.example.Education_for_everyone.dtos.RegisterProfessorDto;
import com.example.Education_for_everyone.exceptions.UserAlreadyExistException;
import com.example.Education_for_everyone.models.Professor;
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
class ProfessorServiceTest {

    @Mock//ne permite sa nu folosim o instanta reala de ProfessorRepository ci putem noi sa definim comportametul lui ProfessorRepository
    private ProfessorRepository professorRepository;
    @Mock
    private SendEmailService sendEmailService;
    @Mock
    private KeycloakAdminService keycloakAdminService;

    @Autowired
    private ProfessorService professorService;

    @BeforeEach //avem adnotarea asta care se apeleaza inaintea fiecarui test
    void before() {
        professorService = new ProfessorService(professorRepository,sendEmailService,keycloakAdminService);
    }

    @Test
    void registerShouldFailIfUsernameIsAlreadyTaken() {

        //Arrange

        String username = "user1";
        Professor professor=Professor.builder().username(username).build();
        RegisterProfessorDto registerProfessorDto=RegisterProfessorDto.builder().username(username).build();

        //Act

        when(professorRepository.findByUsername(username)).thenReturn(Optional.of(professor)); /* definesc ce se intampla cand se apeleaza professorRepository.findByUsername
        si anume o sa returnez un optional de professor ptc asta returneaza findByUsername. Astfel if-ul din ProfessorService va fi true si se va arunca o exceptie*/


        //Assert

        //eu verific cazul in care profesorul exista cu username-ul ala asa ca ma astept ca metoda mea sa arunce o exceptie deci rezultatul dorit ar fi sa arunce o exceptie
        Assertions.assertThrows(UserAlreadyExistException.class, () -> professorService.registerProfessor(registerProfessorDto));


        //verific daca se face interactiunea cu findByUsername doar o data
        verify(professorRepository, times(1)).findByUsername(username);

        //vreau sa ma asigur ca professorRepository.save(professor) nu s-a apelat (ca nu s-a fct save) si ca flow-ul s-a intrerupt dupa ce am aruncat exceptia
        verify(professorRepository, never()).save(any(Professor.class));

    }

    @Test //in testul asta verificam ca registerul se face cu succes
    void registerShouldSucceed() {

        //Arrange
        String username = "user2";
        RegisterProfessorDto registerProfessorDto=RegisterProfessorDto.builder().username(username).build();

        //Act
        when(professorRepository.findByUsername(username)).thenReturn(Optional.empty());/* definesc ce se intampla cand se apeleaza professorRepository.findByUsername si anume
        returnez un optional empty ca sa nu mai fie prezent un professor adica sa nu se fi gasit un professor cu username-ul ala si asa nu s-ar mai arunca exceptia din if */

        professorService.registerProfessor(registerProfessorDto);


        //Assert
        verify(professorRepository, times(1)).findByUsername(username);//verific daca se face interactiunea cu findByUsername doar o data
        verify(professorRepository, times(1)).save(any(Professor.class));//verific daca save-ul sa se apeleze o data
    }

    @Test
    void getUserShouldSucceed() {
        //Arrange
        String email = "random@email";
        String firstName = "First name";
        String username = "Username";
        String lastName="Last name";
        Professor professor = Professor.builder().email(email).firstName(firstName).lastName(lastName).username(username).build();

        //Act
        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor)); //definesc un comportament pt id-ul 1

        GetProfessorDto result = professorService.getProfessor(1L);


        //Assert
        Assertions.assertEquals(email,result.getEmail()); //rezultatul meu trebuie sa aiba emailul egal cu emailul pe care il definesc eu in Arrange
        Assertions.assertEquals(firstName,result.getFirstName());//rezultatul meu trebuie sa aiba firstName egal cu firstName pe care il definesc eu in Arrange
        Assertions.assertEquals(lastName,result.getLastName());//rezultatul meu trebuie sa aiba lastName egal cu lastName pe care il definesc eu in Arrange
        verify(professorRepository).findById(anyLong());
    }

    @Test
    void deleteUserShouldSucceed() {
        //Arrange
        Long id=1L;
        String email = "random@email";
        String firstName = "First name";
        String username = "Username";
        String lastName="Last name";
        Professor professor = Professor.builder().id(id).email(email).firstName(firstName).lastName(lastName).username(username).build();

        //Act
        when(professorRepository.findById(professor.getId())).thenReturn(Optional.of(professor));
        //cand cauta profesorul dupa id returnez un optional de professor ptc l-a gasit in baza de date

        professorService.removeProfessor(professor.getId(),username);

        //Assert
        verify(professorRepository, times(1)).findById(professor.getId());//verific daca se face interactiunea cu findById doar o data
        verify(professorRepository,times(1)).delete(professor);//verific daca delete-ul sa se apeleze o data

    }


}