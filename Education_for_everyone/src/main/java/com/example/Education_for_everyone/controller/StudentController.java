package com.example.Education_for_everyone.controller;


import com.example.Education_for_everyone.SendEmailService;
import com.example.Education_for_everyone.dtos.GetProfessorDto;
import com.example.Education_for_everyone.dtos.GetStudentDto;
import com.example.Education_for_everyone.dtos.RegisterStudentDto;
import com.example.Education_for_everyone.service.StudentService;
import com.example.Education_for_everyone.utils.Helper;
import com.example.Education_for_everyone.utils.SuccessDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController


@RequestMapping("/student")
public class StudentController {

    private StudentService studentService;


    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;

    }


    @PostMapping("/register")
    @SneakyThrows
    public ResponseEntity<SuccessDto>registerStudent(@RequestBody RegisterStudentDto registerStudentDto)
    {
        studentService.registerStudent(registerStudentDto);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @DeleteMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>removeStudent(@RequestParam Long studentId, Authentication authentication)
    {
        studentService.removeStudent(studentId, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/all")
    @SneakyThrows
    public ResponseEntity<SuccessDto>removeAllStudents()
    {
        studentService.removeAllStudents();

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    @SneakyThrows
    public ResponseEntity<GetStudentDto>getStudent(@RequestParam Long studentId)
    {
        return new ResponseEntity<>(studentService.getStudent(studentId), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @PutMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>putStudent(@RequestParam Long studentId,@RequestBody RegisterStudentDto registerStudentDto,Authentication authentication)
    {
        studentService.putStudent(studentId,registerStudentDto,Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    //afisam toti studentii
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/showStudents")
    public ResponseEntity<List<GetStudentDto>> getAllAllStudents() {

        return new ResponseEntity<>( studentService.getAllStudents(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/showStudentsByName")
    public ResponseEntity<List<GetStudentDto>> getAllStudentsByName(@RequestParam String studentName) {

        return new ResponseEntity<>(studentService.getAllStudentsByName(studentName), HttpStatus.OK);
    }


}
