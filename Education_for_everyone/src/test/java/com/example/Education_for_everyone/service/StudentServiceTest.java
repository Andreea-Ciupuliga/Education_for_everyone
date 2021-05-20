package com.example.Education_for_everyone.service;

import com.example.Education_for_everyone.SendEmailService;
import com.example.Education_for_everyone.dtos.GetStudentDto;
import com.example.Education_for_everyone.dtos.RegisterStudentDto;
import com.example.Education_for_everyone.exceptions.UserAlreadyExistException;
import com.example.Education_for_everyone.models.Student;
import com.example.Education_for_everyone.repository.*;
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
class StudentServiceTest {

    @Mock//ne permite sa nu folosim o instanta reala de StudentRepository ci putem noi sa definim comportametul lui StudentRepository
    private StudentRepository studentRepository;
    @Mock
    private SendEmailService sendEmailService;
    @Mock
    private KeycloakAdminService keycloakAdminService;
    @Mock
    private GroupOfStudentsRepository groupOfStudentsRepository;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private StudentsBorrowBooksRepository studentsBorrowBooksRepository;
    @Mock
    private BookRepository bookRepository;

    @Autowired
    private StudentService studentService; /*StudentService se foloseste de StudentRepository si aici e o problema deoarece pot aparea prob la userRepository si noi
    o sa credem ca sunt de la StudentService. Practic pe mine nu ma intereseaza sa testez si partea de StudentRepository ci doar StudentService.Pentru asta avem conceptul de @Mock
    */

    @BeforeEach //avem adnotarea asta care se apeleaza inaintea fiecarui test
    void before() {
        studentService = new StudentService(studentRepository,sendEmailService,keycloakAdminService,groupOfStudentsRepository,groupRepository,studentsBorrowBooksRepository,bookRepository);
    }

    @Test//daca exista un user cu username-ul asta in baza de date sa se arunce o exceptie
    void registerShouldFailIfUsernameIsAlreadyTaken() {

        //Arrange

        String username = "user1";
        Student student=Student.builder().username(username).build();
        RegisterStudentDto registerStudentDto=RegisterStudentDto.builder().username(username).build();

        //Act

        when(studentRepository.findByUsername(username)).thenReturn(Optional.of(student));/* definesc ce se intampla cand se apeleaza studentRepository.findByUsername
        si anume o sa returnez un optional de student ptc asta returneaza findByUsername. Astfel if-ul din StudentService va fi true si se va arunca o exceptie*/


        //Assert

        //eu verific cazul in care studentul exista cu username-ul ala asa ca ma astept ca metoda mea sa arunce o exceptie deci rezultatul dorit ar fi sa arunce o exceptie
        Assertions.assertThrows(UserAlreadyExistException.class, () -> studentService.registerStudent(registerStudentDto));


        //verific daca se face interactiunea cu findByUsername doar o data
        verify(studentRepository, times(1)).findByUsername(username);

        //vreau sa ma asigur ca studentRepository.save(student) nu s-a apelat (ca nu s-a fct save) si ca flow-ul s-a intrerupt dupa ce am aruncat exceptia
        verify(studentRepository, never()).save(any(Student.class));

    }

    @Test //in testul asta verificam ca registerul se face cu succes
    void registerShouldSucceed() {

        //Arrange
        String username = "user1";
        RegisterStudentDto registerStudentDto=RegisterStudentDto.builder().username(username).build();

        //Act
        when(studentRepository.findByUsername(username)).thenReturn(Optional.empty());/* definesc ce se intampla cand se apeleaza studentRepository.findByUsername si anume
        returnez un optional empty ca sa nu mai fie prezent un student adica sa nu se fi gasit un user cu emailul ala si asa nu s-ar mai arunca exceptia din if */

        studentService.registerStudent(registerStudentDto);


        //Assert
        verify(studentRepository, times(1)).findByUsername(username);//verific daca se face interactiunea cu findByUsername doar o data
        verify(studentRepository, times(1)).save(any(Student.class));//verific daca save-ul sa se apeleze o datea

    }

    @Test
    void getUserShouldSucceed() {
        //Arrange
        String email = "random@email";
        String firstName = "First name";
        String username = "Username";
        String lastName="Last name";
        Student student = Student.builder().email(email).firstName(firstName).lastName(lastName).username(username).build();

        //Act
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student)); //definesc un comportament pt id-ul 1

        GetStudentDto result = studentService.getStudent(1L);


        //Assert
        Assertions.assertEquals(email,result.getEmail()); //rezultatul meu trebuie sa aiba emailul egal cu emailul pe care il definesc eu in Arrange
        Assertions.assertEquals(firstName,result.getFirstName());//rezultatul meu trebuie sa aiba firstName egal cu firstName pe care il definesc eu in Arrange
        Assertions.assertEquals(lastName,result.getLastName());//rezultatul meu trebuie sa aiba lastName egal cu lastName pe care il definesc eu in Arrange
        verify(studentRepository).findById(anyLong());
    }

}