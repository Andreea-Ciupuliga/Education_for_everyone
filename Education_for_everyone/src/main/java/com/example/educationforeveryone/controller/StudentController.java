package com.example.educationforeveryone.controller;

import com.example.educationforeveryone.dtos.GetStudentDto;
import com.example.educationforeveryone.dtos.RegisterStudentDto;
import com.example.educationforeveryone.service.StudentService;
import com.example.educationforeveryone.utils.Helper;
import com.example.educationforeveryone.utils.SuccessDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@Tag(name = "/students", description = "student controller")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;

    }

    @Operation(summary = "Register a student", description = "Register a new student")
    @PostMapping("/register")
    public ResponseEntity<SuccessDto> registerStudent(@RequestBody RegisterStudentDto registerStudentDto) {
        studentService.registerStudent(registerStudentDto);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Remove a student", description = "Remove a student by ID")
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @DeleteMapping("/{studentId}")
    public ResponseEntity<SuccessDto> removeStudent(@PathVariable Long studentId, Authentication authentication) {
        studentService.removeStudent(studentId, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Remove all students", description = "Remove all students")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/all")
    public ResponseEntity<SuccessDto> removeAllStudents() {
        studentService.removeAllStudents();
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Get student by ID", description = "Get a student by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{studentId}")
    public ResponseEntity<GetStudentDto> getStudentById(@PathVariable Long studentId) {
        return new ResponseEntity<>(studentService.getStudentById(studentId), HttpStatus.OK);
    }

    @Operation(summary = "Update a student", description = "Update a student by ID")
    @PreAuthorize("hasAnyRole('ADMIN','STUDENT')")
    @PutMapping("/{studentId}")
    public ResponseEntity<SuccessDto> updateStudent(@PathVariable Long studentId, @RequestBody RegisterStudentDto registerStudentDto, Authentication authentication) {
        studentService.updateStudent(studentId, registerStudentDto, Helper.getKeycloakUser(authentication));
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Get all students", description = "Get all students")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping
    public ResponseEntity<List<GetStudentDto>> getAllStudents() {
        return new ResponseEntity<>(studentService.getAllStudents(), HttpStatus.OK);
    }

    @Operation(summary = "Get students by name", description = "Get students by name")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/by-name/{studentName}")
    public ResponseEntity<List<GetStudentDto>> getAllStudentsByName(@PathVariable String studentName) {
        return new ResponseEntity<>(studentService.getAllStudentsByName(studentName), HttpStatus.OK);
    }

}
