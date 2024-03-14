package com.example.educationforeveryone.service;

import com.example.educationforeveryone.dtos.GetBookDto;
import com.example.educationforeveryone.dtos.RegisterBookDto;
import com.example.educationforeveryone.exceptions.alreadyExistException.BookAlreadyExistException;
import com.example.educationforeveryone.exceptions.notFoundException.BookNotFoundException;
import com.example.educationforeveryone.models.Book;
import com.example.educationforeveryone.repository.BookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void registerBook(RegisterBookDto registerBookDto) {
        if (bookRepository.findByTitle(registerBookDto.getTitle()).isPresent()) {
            throw new BookAlreadyExistException("Book with this title already exist!");
        }
        Book savedBook = bookRepository.save(buildBook(registerBookDto));
        log.info("Successfully saved book with id: {}", savedBook.getId());
    }

    public void removeBook(Long bookId) {
        Book book = findBookByIdOrThrowNotFoundException(bookId);
        bookRepository.delete(book);
        log.info("Successfully removed book with id: {}", bookId);
    }

    public GetBookDto getBookById(Long bookId) {
        Book book = findBookByIdOrThrowNotFoundException(bookId);
        return buildGetBookDto(book);
    }

    public void updateBook(Long bookId, RegisterBookDto newRegisterBookDto) {
        Book book = findBookByIdOrThrowNotFoundException(bookId);
        setFieldsIfNotNull(newRegisterBookDto, book);
        bookRepository.save(book);
        log.info("Successfully updated book with id: {}", bookId);
    }

    public List<GetBookDto> getAllBooks() {
        List<GetBookDto> allBooks = bookRepository.findAllBooks();
        if (allBooks.isEmpty()) {
            throw new BookNotFoundException("There are no books to display");
        }
        return allBooks;
    }

    public List<GetBookDto> getAllBooksByTitle(String title) {
        List<GetBookDto> booksByTitle = bookRepository.findAllByTitleContains(title);
        if (booksByTitle.isEmpty()) {
            throw new BookNotFoundException("Title Not Found");
        }
        return booksByTitle;
    }

    public List<GetBookDto> getAllBooksByAuthor(String author) {
        List<GetBookDto> booksByAuthor = bookRepository.findAllByAuthorContains(author);
        if (booksByAuthor.isEmpty()) {
            throw new BookNotFoundException("Author Not Found");
        }
        return booksByAuthor;
    }

    private void setFieldsIfNotNull(RegisterBookDto newRegisterBookDto, Book book) {
        if (newRegisterBookDto.getTitle() != null) {
            book.setTitle(newRegisterBookDto.getTitle());
        }
        if (newRegisterBookDto.getAuthor() != null) {
            book.setAuthor(newRegisterBookDto.getAuthor());
        }
        if (newRegisterBookDto.getAvailableCopies() != null) {
            book.setAvailableCopies(newRegisterBookDto.getAvailableCopies());
        }
        if (newRegisterBookDto.getTotalCopies() != null) {
            book.setTotalCopies(newRegisterBookDto.getTotalCopies());
        }
    }

    private Book findBookByIdOrThrowNotFoundException(Long bookId) {
        return bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book not found"));
    }

    private Book buildBook(RegisterBookDto registerBookDto) {
        return Book.builder()
                .title(registerBookDto.getTitle())
                .author(registerBookDto.getAuthor())
                .availableCopies(registerBookDto.getTotalCopies()) //At the beginning, the available copies should be equal to the total copies since no one has rented anything yet
                .totalCopies(registerBookDto.getTotalCopies()).build();
    }

    private GetBookDto buildGetBookDto(Book book) {
        return GetBookDto.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .availableCopies(book.getAvailableCopies()).build();
    }

}
