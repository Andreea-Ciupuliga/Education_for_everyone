package com.example.educationforeveryone.controller;

import com.example.educationforeveryone.service.StudentBookService;
import com.example.educationforeveryone.utils.Helper;
import com.example.educationforeveryone.utils.SuccessDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student-books")
@Tag(name = "/student-books", description = "student-book controller")
public class StudentBookController {

    private final StudentBookService studentBookService;

    public StudentBookController(StudentBookService studentBookService) {
        this.studentBookService = studentBookService;
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @PostMapping("/register/{bookId}")
    public ResponseEntity<SuccessDto> registerStudentBook(Authentication authentication, @PathVariable Long bookId) {
        studentBookService.registerStudentBook(Helper.getKeycloakUser(authentication), bookId);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @DeleteMapping("/{bookId}")
    public ResponseEntity<SuccessDto> removeStudentBook(Authentication authentication, @PathVariable Long bookId) {
        studentBookService.removeStudentBook(Helper.getKeycloakUser(authentication), bookId);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/{studentId}")
    public List<String> getAllStudentBooks(@PathVariable Long studentId) {
        return studentBookService.getAllStudentBooks(studentId);
    }

}
