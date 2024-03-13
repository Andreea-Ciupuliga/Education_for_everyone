package com.example.educationforeveryone.controller;

import com.example.educationforeveryone.dtos.GetBookDto;
import com.example.educationforeveryone.dtos.RegisterBookDto;
import com.example.educationforeveryone.service.BookService;
import com.example.educationforeveryone.utils.SuccessDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@Tag(name = "/books", description = "book controller")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @Operation(summary = "Register a book", description = "Register a book")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<SuccessDto> registerBook(@RequestBody RegisterBookDto registerBookDto) {
        bookService.registerBook(registerBookDto);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Remove a book", description = "Remove a book by book id")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{bookId}")
    public ResponseEntity<SuccessDto> removeBook(@PathVariable Long bookId) {
        bookService.removeBook(bookId);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Get a book by id", description = "Get a book by book id")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/{bookId}")
    public ResponseEntity<GetBookDto> getBookById(@PathVariable Long bookId) {
        return new ResponseEntity<>(bookService.getBook(bookId), HttpStatus.OK);
    }

    @Operation(summary = "Update a book", description = "Update a book")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{bookId}")
    public ResponseEntity<SuccessDto> updateBook(@PathVariable Long bookId, @RequestBody RegisterBookDto registerBookDto) {
        bookService.updateBook(bookId, registerBookDto);
        return new ResponseEntity<>(new SuccessDto(), HttpStatus.OK);
    }

    @Operation(summary = "Get all books", description = "Get a list with all books")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping
    public ResponseEntity<List<GetBookDto>> getAllBooks() {
        return new ResponseEntity<>(bookService.getAllBooks(), HttpStatus.OK);
    }

    @Operation(summary = "Get all books by title", description = "Get all books by title")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/by-title/{title}")
    public ResponseEntity<List<GetBookDto>> getAllBooksByTitle(@PathVariable String title) {
        return new ResponseEntity<>(bookService.getAllBooksByTitle(title), HttpStatus.OK);
    }

    @Operation(summary = "Get all books by author", description = "Get all books by author")
    @PreAuthorize("hasAnyRole('ADMIN','PROFESSOR','STUDENT')")
    @GetMapping("/by-author/{author}")
    public ResponseEntity<List<GetBookDto>> getAllBooksByAuthor(@PathVariable String author) {
        return new ResponseEntity<>(bookService.getAllBooksByAuthor(author), HttpStatus.OK);
    }
}