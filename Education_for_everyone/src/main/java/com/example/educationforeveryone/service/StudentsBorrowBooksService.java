package com.example.educationforeveryone.service;

import com.example.educationforeveryone.exceptions.DoNotMatchException;
import com.example.educationforeveryone.exceptions.StudentsBorrowBooksException;
import com.example.educationforeveryone.exceptions.alreadyExistException.UserAlreadyExistException;
import com.example.educationforeveryone.exceptions.notFoundException.BookNotFoundException;
import com.example.educationforeveryone.exceptions.notFoundException.UserNotFoundException;
import com.example.educationforeveryone.models.Book;
import com.example.educationforeveryone.models.Student;
import com.example.educationforeveryone.models.StudentsBorrowBooks;
import com.example.educationforeveryone.repository.BookRepository;
import com.example.educationforeveryone.repository.StudentRepository;
import com.example.educationforeveryone.repository.StudentsBorrowBooksRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentsBorrowBooksService {

    private final StudentRepository studentRepository;
    private final BookRepository bookRepository;
    private final StudentsBorrowBooksRepository studentsBorrowBooksRepository;

    public StudentsBorrowBooksService(StudentRepository studentRepository,
                                      BookRepository bookRepository,
                                      StudentsBorrowBooksRepository studentsBorrowBooksRepository) {
        this.studentRepository = studentRepository;
        this.bookRepository = bookRepository;
        this.studentsBorrowBooksRepository = studentsBorrowBooksRepository;
    }

    public void borrowBook(String username, Long bookId) {
        Student student = studentRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("student not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("book not found"));

        //daca gasim perechea studentId si bookId inseamna ca are inchiriata cartea asta
        if (studentsBorrowBooksRepository.findByStudentIdAndBookId(student.getId(), bookId).isPresent()) {
            throw new UserAlreadyExistException("student has already borrowed this book");
        }

        if (book.getAvailableCopies() <= 0) {
            throw new StudentsBorrowBooksException("Not Enough Available Copies");
        }

        StudentsBorrowBooks studentsBorrowBooks = StudentsBorrowBooks.builder()
                .student(Student.builder().id(student.getId()).build())
                .book(Book.builder().id(book.getId()).build())
                .title(book.getTitle())
                .studentFirstName(student.getFirstName())
                .studentLastName(student.getLastName()).build();

        studentsBorrowBooksRepository.save(studentsBorrowBooks);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

    }

    public void removeBookFromStudent(String username, Long bookId) {

        Student student = studentRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("student not found"));

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("book not found"));

        //verificam ca exista perechea studentId si bookId
        StudentsBorrowBooks studentsBorrowBooks = studentsBorrowBooksRepository.findByStudentIdAndBookId(student.getId(), bookId).orElseThrow(() -> new DoNotMatchException("Book And Student Do Not Match"));

        studentsBorrowBooksRepository.delete(studentsBorrowBooks);

        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

    }

    //aratam toate cartile inchiriate de un student
    public List<String> booksBorrowedByStudent(Long studentId) {

        if (studentRepository.findById(studentId).isEmpty()) {
            throw new UserNotFoundException("student not found");
        }

        return studentsBorrowBooksRepository.findTitleByStudentId(studentId);
    }

}
