package com.example.Education_for_everyone.service;

import com.example.Education_for_everyone.SendEmailService;
import com.example.Education_for_everyone.dtos.GetStudentDto;
import com.example.Education_for_everyone.dtos.RegisterStudentDto;
import com.example.Education_for_everyone.exceptions.BookNotFoundException;
import com.example.Education_for_everyone.exceptions.GroupNotFoundException;
import com.example.Education_for_everyone.exceptions.UserAlreadyExistException;
import com.example.Education_for_everyone.exceptions.UserNotFoundException;
import com.example.Education_for_everyone.models.Book;
import com.example.Education_for_everyone.models.Group;
import com.example.Education_for_everyone.models.Student;
import com.example.Education_for_everyone.repository.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {

    private StudentRepository studentRepository;
    private SendEmailService sendEmailService;
    private final KeycloakAdminService keycloakAdminService; //ca sa pot face salvarea de useri si in keycloak
    private GroupOfStudentsRepository groupOfStudentsRepository;
    private GroupRepository groupRepository;
    private StudentsBorrowBooksRepository studentsBorrowBooksRepository;
    private BookRepository bookRepository;


    @Autowired
    public StudentService(StudentRepository studentRepository, SendEmailService sendEmailService, KeycloakAdminService keycloakAdminService,GroupOfStudentsRepository groupOfStudentsRepository,GroupRepository groupRepository,StudentsBorrowBooksRepository studentsBorrowBooksRepository,BookRepository bookRepository) {
        this.studentRepository = studentRepository;
        this.sendEmailService = sendEmailService;
        this.keycloakAdminService = keycloakAdminService;
        this.groupOfStudentsRepository=groupOfStudentsRepository;
        this.groupRepository=groupRepository;
        this.studentsBorrowBooksRepository=studentsBorrowBooksRepository;
        this.bookRepository=bookRepository;
    }


    @SneakyThrows
    public void registerStudent(RegisterStudentDto registerStudentDto)
    {

        if(studentRepository.findByUsername(registerStudentDto.getUsername()).isPresent())
        {
            throw new UserAlreadyExistException("Username Already Exist");
        }

        if(studentRepository.findByEmail(registerStudentDto.getEmail()).isPresent())
        {
            throw new UserAlreadyExistException("Email Already Exist");
        }

        //trimitem mail ca s-a inregistrat
        String body="Hello "+registerStudentDto.getFirstName()+" "+registerStudentDto.getLastName()+" you registered with your username: "+registerStudentDto.getUsername();
        sendEmailService.sendEmail(registerStudentDto.getEmail(),body,"Inregistrare");

        Student student= Student.builder()
                .firstName(registerStudentDto.getFirstName())
                .lastName(registerStudentDto.getLastName())
                .email(registerStudentDto.getEmail())
                .password(registerStudentDto.getPassword())
                .username(registerStudentDto.getUsername()).build();

        studentRepository.save(student);

        //specificam direct ROLE_STUDENT ptc nu vrem ca cine inregistreaza din postman sa poata decide ce rol sa aiba
        keycloakAdminService.registerUser(registerStudentDto.getUsername(), registerStudentDto.getPassword(), "ROLE_STUDENT");

    }


    @SneakyThrows
    public void removeStudent(Long studentId,String username)
    {
        Student student = studentRepository.findById(studentId).orElseThrow(()->new UserNotFoundException("student not found"));

        //daca este admin cel care vrea sa stearga studentul sau daca este chiar studentul
        if(username.equals("admin") || username.equals(student.getUsername()))
        {

            //vedem lista de grupuri din care face parte studentul (id urile grupurilor mai exact) ptc daca stergem studentul vrem sa il stergem din grupurile din care face parte
            ArrayList groupIds = groupOfStudentsRepository.findGroupIdByStudentId(studentId);

            //pentru fiecare grup in parte crestem numarul de locuri libere ptc vom sterge studentul care facea partea din ele
            for (int i = 0; i < groupIds.size(); i++) {
                Group group = groupRepository.findById((Long) groupIds.get(i)).orElseThrow(() -> new GroupNotFoundException("group not found"));
                group.setAvailableSeats(group.getAvailableSeats() + 1);
                groupRepository.save(group);
            }

            //vedem lista de carti pe care le-a imprumutat studentul (id urile cartilor)
            ArrayList bookIds=studentsBorrowBooksRepository.findBookIdByStudentId(studentId);

            //pentru fiecare carte in parte crestem numarul de copii disponibile ptc vom sterge studentul care le detine
            for (int i = 0; i < bookIds.size(); i++) {
                Book book=bookRepository.findById((Long)bookIds.get(i)).orElseThrow(() -> new BookNotFoundException("book not found"));
                book.setAvailableCopies(book.getAvailableCopies()+1);
                bookRepository.save(book);
            }

            studentRepository.delete(student);
        }

        else if(!(username.equals(student.getUsername())))
            throw new UserNotFoundException("you cannot delete another student's account");

    }

    @SneakyThrows
    public GetStudentDto getStudent(Long studentId)
    {
        Student student = studentRepository.findById(studentId).orElseThrow(()->new UserNotFoundException("student not found"));

        GetStudentDto getStudentDto= GetStudentDto.builder()
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .email(student.getEmail()).build();

        return getStudentDto;
    }

    @SneakyThrows
    public void putStudent(Long studentId,RegisterStudentDto newRegisterStudentDto,String username)
    {
        Student student = studentRepository.findById(studentId).orElseThrow(()->new UserNotFoundException("student not found"));

        //daca este admin sau este chiar studentul
        if(username.equals("admin") || username.equals(student.getUsername())) {

            if (newRegisterStudentDto.getFirstName() != null)
                student.setFirstName(newRegisterStudentDto.getFirstName());

            if (newRegisterStudentDto.getLastName() != null)
                student.setLastName(newRegisterStudentDto.getLastName());

            if (newRegisterStudentDto.getEmail() != null)
                student.setEmail(newRegisterStudentDto.getEmail());

            if (newRegisterStudentDto.getPassword() != null)
                student.setPassword(newRegisterStudentDto.getPassword());

            if (newRegisterStudentDto.getUsername() != null)
                student.setUsername(newRegisterStudentDto.getUsername());


            studentRepository.save(student);
        }

        else if(!(username.equals(student.getUsername())))
            throw new UserNotFoundException("you cannot edit another student's account");

    }


    @SneakyThrows
    public List<GetStudentDto> getAllStudents() {

        if(studentRepository.findAllStudents().isEmpty())
            throw new UserNotFoundException("there are no students to display");

        return studentRepository.findAllStudents();
    }

    @SneakyThrows
    public List<GetStudentDto> getAllStudentsByName(String studentName) {


        if(studentRepository.findAllStudentsByName(studentName).isEmpty())
        {
            throw new UserNotFoundException("Students Not Found");
        }

        return studentRepository.findAllStudentsByName(studentName);
    }

    public void removeAllStudents() {
        studentRepository.deleteAll();
    }
}
