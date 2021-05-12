package com.example.Education_for_everyone.controller;

import com.example.Education_for_everyone.models.Book;
import com.example.Education_for_everyone.service.StudentsBorrowBooksService;
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
@RequestMapping("/studentsBorrowBooks")
public class StudentsBorrowBooksController {

    StudentsBorrowBooksService studentsBorrowBooksService;

    @Autowired
    public StudentsBorrowBooksController(StudentsBorrowBooksService studentsBorrowBooksService) {
        this.studentsBorrowBooksService = studentsBorrowBooksService;
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @PostMapping("/register")
    public ResponseEntity<SuccessDto> borrowBook(Authentication authentication, @RequestParam Long bookId)
    {
        studentsBorrowBooksService.borrowBook(Helper.getKeycloakUser(authentication),bookId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
    @DeleteMapping("")
    @SneakyThrows
    public ResponseEntity<SuccessDto> deleteBookFromStudent(Authentication authentication,@RequestParam Long bookId)
    {
        studentsBorrowBooksService.removeBookFromStudent(Helper.getKeycloakUser(authentication),bookId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/showBooksBorrowedByStudent")
    public List<String> getAllBooksBorrowedByStudent(@RequestParam Long studentId) {

        return studentsBorrowBooksService.booksBorrowedByStudent(studentId);
    }

}
