package com.example.Education_for_everyone.controller;

import com.example.Education_for_everyone.models.Book;
import com.example.Education_for_everyone.service.StudentsBorrowBooksService;
import com.example.Education_for_everyone.utils.SuccessDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    @PostMapping("/register")
    public ResponseEntity<SuccessDto> borrowBook(@RequestParam Long studentId, @RequestParam Long bookId)
    {
        studentsBorrowBooksService.borrowBook(studentId,bookId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @DeleteMapping("")
    @SneakyThrows
    public ResponseEntity<SuccessDto> deleteBookFromStudent(@RequestParam Long studentId,@RequestParam Long bookId)
    {
        studentsBorrowBooksService.removeBookFromStudent(studentId,bookId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @GetMapping("/showBooksBorrowedByStudent")
    public List<String> getAllBooksBorrowedByStudent(@RequestParam Long studentId) {

        return studentsBorrowBooksService.booksBorrowedByStudent(studentId);
    }

}
