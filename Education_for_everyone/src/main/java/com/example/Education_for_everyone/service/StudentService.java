package com.example.Education_for_everyone.service;

import com.example.Education_for_everyone.SendEmailService;
import com.example.Education_for_everyone.dtos.GetProfessorDto;
import com.example.Education_for_everyone.dtos.GetStudentDto;
import com.example.Education_for_everyone.dtos.RegisterStudentDto;
import com.example.Education_for_everyone.exceptions.UserAlreadyExistException;
import com.example.Education_for_everyone.exceptions.UserNotFoundException;
import com.example.Education_for_everyone.models.Student;
import com.example.Education_for_everyone.repository.StudentRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private StudentRepository studentRepository;
    private SendEmailService sendEmailService;
    private final KeycloakAdminService keycloakAdminService; //ca sa pot face salvarea de useri si in keycloak


    @Autowired
    public StudentService(StudentRepository studentRepository, SendEmailService sendEmailService, KeycloakAdminService keycloakAdminService) {
        this.studentRepository = studentRepository;
        this.sendEmailService = sendEmailService;
        this.keycloakAdminService = keycloakAdminService;
    }






    @SneakyThrows
    public void registerStudent(RegisterStudentDto registerStudentDto)
    {

        if(studentRepository.findByUsername(registerStudentDto.getUsername()).isPresent())
        {

            throw new UserAlreadyExistException("Student Already Exist");

        }

        String body="Hello "+registerStudentDto.getFirstName()+" "+registerStudentDto.getLastName()+" you registered with your username: "+registerStudentDto.getUsername();
        sendEmailService.sendEmail(registerStudentDto.getEmail(),body,"Inregistrare");

        Student student= Student.builder()
                .firstName(registerStudentDto.getFirstName())
                .lastName(registerStudentDto.getLastName())
                .email(registerStudentDto.getEmail())
                .password(registerStudentDto.getPassword())
                .username(registerStudentDto.getUsername()).build();

        studentRepository.save(student);


        keycloakAdminService.registerUser(registerStudentDto.getUsername(), registerStudentDto.getPassword(), "ROLE_STUDENT");
        //specificam direct ROLE_STUDENT ptc nu vrem ca cine inregistreaza din postman sa poata decide ce rol sa aiba

    }


    @SneakyThrows
    public void removeStudent(Long studentId,String username)
    {
        Student student = studentRepository.findById(studentId).orElseThrow(()->new UserNotFoundException("student not found"));

        if(username.equals("admin") || username.equals(student.getUsername()))
            studentRepository.delete(student);

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

}
