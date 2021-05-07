package com.example.Education_for_everyone.service;

import com.example.Education_for_everyone.SendEmailService;
import com.example.Education_for_everyone.dtos.GetStudentDto;
import com.example.Education_for_everyone.dtos.RegisterStudentDto;
import com.example.Education_for_everyone.exceptions.UserAlreadyExistException;
import com.example.Education_for_everyone.exceptions.UserNotFoundException;
import com.example.Education_for_everyone.models.Student;
import com.example.Education_for_everyone.repository.StudentRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private StudentRepository studentRepository;
    private SendEmailService sendEmailService;

    @Autowired
    public StudentService(StudentRepository studentRepository, SendEmailService sendEmailService) {
        this.studentRepository = studentRepository;
        this.sendEmailService = sendEmailService;
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

    }


    @SneakyThrows
    public void removeStudent(Long studentId)
    {
        Student student = studentRepository.findById(studentId).orElseThrow(()->new UserNotFoundException("student not found"));
        studentRepository.delete(student);
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
    public void putStudent(Long studentId,RegisterStudentDto newRegisterStudentDto)
    {
        Student student = studentRepository.findById(studentId).orElseThrow(()->new UserNotFoundException("student not found"));

        if(newRegisterStudentDto.getFirstName()!=null)
            student.setFirstName(newRegisterStudentDto.getFirstName());

        if(newRegisterStudentDto.getLastName()!=null)
            student.setLastName(newRegisterStudentDto.getLastName());

        if(newRegisterStudentDto.getEmail()!=null)
            student.setEmail(newRegisterStudentDto.getEmail());

        if(newRegisterStudentDto.getPassword()!=null)
            student.setPassword(newRegisterStudentDto.getPassword());

        if(newRegisterStudentDto.getUsername()!=null)
            student.setUsername(newRegisterStudentDto.getUsername());


        studentRepository.save(student);

    }




}
