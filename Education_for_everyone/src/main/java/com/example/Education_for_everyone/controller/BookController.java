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

    @PostMapping("/register")
    @SneakyThrows
    public ResponseEntity<SuccessDto> registerBook(@RequestBody RegisterBookDto registerBookDto)
    {
        bookService.registerBook(registerBookDto);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }


    @DeleteMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>removeBook(@RequestParam Long bookId)
    {
        bookService.removeBook(bookId);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @GetMapping()
    @SneakyThrows
    public ResponseEntity<GetBookDto>getBook(@RequestParam Long bookId)
    {

        return new ResponseEntity<>(bookService.getBook(bookId), HttpStatus.OK);
    }

    @PutMapping()
    @SneakyThrows
    public ResponseEntity<SuccessDto>putBook(@RequestParam Long bookId,@RequestBody RegisterBookDto registerBookDto)
    {
        bookService.putBook(bookId,registerBookDto);

        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }


    @GetMapping("/showBooksByTitle")
    public List<GetBookDto> getAllBooksByTitle(@RequestParam String title) {

        return bookService.getAllBooksByTitle(title);
    }

    @GetMapping("/showBooksByAuthor")
    public List<GetBookDto> getAllBooksByAuthor(@RequestParam String author) {

        return bookService.getAllBooksByAuthor(author);
    }

}
