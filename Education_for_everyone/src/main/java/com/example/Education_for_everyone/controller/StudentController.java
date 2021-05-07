package com.example.Education_for_everyone.controller;


import com.example.Education_for_everyone.SendEmailService;
import com.example.Education_for_everyone.dtos.GetStudentDto;
import com.example.Education_for_everyone.dtos.RegisterStudentDto;
import com.example.Education_for_everyone.service.StudentService;
import com.example.Education_for_everyone.utils.SuccessDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>removeStudent(@RequestParam Long studentId)
    {
        studentService.removeStudent(studentId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

//    @DeleteMapping("/all")
//    @SneakyThrows
//    public ResponseEntity<SuccessDto>removeAllStudents()
//    {
//        studentService.removeAllStudents();
//
//        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
//    }

    @GetMapping()
    @SneakyThrows
    public ResponseEntity<GetStudentDto>getStudent(@RequestParam Long studentId)
    {

        return new ResponseEntity<>(studentService.getStudent(studentId), HttpStatus.OK);
    }


    @PutMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>putStudent(@RequestParam Long studentId,@RequestBody RegisterStudentDto registerStudentDto)
    {
        studentService.putStudent(studentId,registerStudentDto);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

}
