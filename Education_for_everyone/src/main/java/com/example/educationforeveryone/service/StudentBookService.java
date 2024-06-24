package com.example.educationforeveryone.service;

import com.example.educationforeveryone.exceptions.DoNotMatchException;
import com.example.educationforeveryone.exceptions.StudentsBorrowBooksException;
import com.example.educationforeveryone.exceptions.alreadyExistException.UserAlreadyExistException;
import com.example.educationforeveryone.models.Book;
import com.example.educationforeveryone.models.Student;
import com.example.educationforeveryone.models.StudentBook;
import com.example.educationforeveryone.repository.BookRepository;
import com.example.educationforeveryone.repository.StudentBookRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class StudentBookService {

    private final StudentService studentService;
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final StudentBookRepository studentBookRepository;

    public StudentBookService(StudentService studentService,
                              BookService bookService, BookRepository bookRepository,
                              StudentBookRepository studentBookRepository) {
        this.studentService = studentService;
        this.bookService = bookService;
        this.bookRepository = bookRepository;
        this.studentBookRepository = studentBookRepository;
    }

    public void registerStudentBook(String username, Long bookId) {
        Student student = studentService.getStudentByUsernameOrThrowException(username);
        Book book = bookService.getBookByIdOrThrowNotFoundException(bookId);
        checkIfStudentAlreadyBorrowedBook(bookId, student);
        checkBookAvailability(book);
        StudentBook studentBook = studentBookRepository.save(buildStudentsBorrowBooks(student, book));
        updateBookAvailability(book, -1);
        log.info("Successfully saved studentBook with id: {} where book id: {} and student: {}", studentBook.getId(), bookId, username);
    }

    public void removeStudentBook(String username, Long bookId) {
        Student student = studentService.getStudentByUsernameOrThrowException(username);
        Book book = bookService.getBookByIdOrThrowNotFoundException(bookId);
        StudentBook studentBook = getByStudentIdAndBookId(bookId, student).orElseThrow(() -> new DoNotMatchException("Book and student do not match!"));
        studentBookRepository.delete(studentBook);
        updateBookAvailability(book, 1);
        log.info("Successfully removed studentBook with id: {} where book id: {} and student: {}", studentBook.getId(), bookId, username);
    }

    public List<String> getAllStudentBooks(Long studentId) {
        studentService.getStudentByIdOrThrowException(studentId);
        return studentBookRepository.findTitleByStudentId(studentId);
    }

    private Optional<StudentBook> getByStudentIdAndBookId(Long bookId, Student student) {
        return studentBookRepository.findByStudentIdAndBookId(student.getId(), bookId);
    }

    private void updateBookAvailability(Book book, int change) {
        book.setAvailableCopies(book.getAvailableCopies() + change);
        bookRepository.save(book);
    }

    private void checkBookAvailability(Book book) {
        if (book.getAvailableCopies() <= 0) {
            throw new StudentsBorrowBooksException("Not enough available copies!");
        }
    }

    private void checkIfStudentAlreadyBorrowedBook(Long bookId, Student student) {
        if (getByStudentIdAndBookId(bookId, student).isPresent()) {
            throw new UserAlreadyExistException("Student has already borrowed this book!");
        }
    }

    private StudentBook buildStudentsBorrowBooks(Student student, Book book) {
        return StudentBook.builder()
                .student(Student.builder().id(student.getId()).build())
                .book(Book.builder().id(book.getId()).build())
                .title(book.getTitle())
                .studentFirstName(student.getFirstName())
                .studentLastName(student.getLastName()).build();
    }

}
