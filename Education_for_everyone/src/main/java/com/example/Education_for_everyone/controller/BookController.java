package com.example.Education_for_everyone.controller;


import com.example.Education_for_everyone.dtos.GetBookDto;
import com.example.Education_for_everyone.dtos.RegisterBookDto;
import com.example.Education_for_everyone.models.Book;
import com.example.Education_for_everyone.models.Group;
import com.example.Education_for_everyone.service.BookService;
import com.example.Education_for_everyone.utils.SuccessDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/book")
public class BookController {

    private BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    @SneakyThrows
    public ResponseEntity<SuccessDto> registerBook(@RequestBody RegisterBookDto registerBookDto)
    {
        bookService.registerBook(registerBookDto);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>removeBook(@RequestParam Long bookId)
    {
        bookService.removeBook(bookId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping()
    @SneakyThrows
    public ResponseEntity<GetBookDto>getBook(@RequestParam Long bookId)
    {
        return new ResponseEntity<>(bookService.getBook(bookId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>putBook(@RequestParam Long bookId,@RequestBody RegisterBookDto registerBookDto)
    {
        bookService.putBook(bookId,registerBookDto);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/showBooks")
    public ResponseEntity<List<GetBookDto>> getAllBooks() {

        return new ResponseEntity<>(bookService.getAllBooks(), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/showBooksByTitle")
    public ResponseEntity<List<GetBookDto>> getAllBooksByTitle(@RequestParam String title) {

        return new ResponseEntity<>(bookService.getAllBooksByTitle(title), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/showBooksByAuthor")
    public ResponseEntity<List<GetBookDto>> getAllBooksByAuthor(@RequestParam String author) {

        return new ResponseEntity<>(bookService.getAllBooksByAuthor(author), HttpStatus.OK);
    }

}
