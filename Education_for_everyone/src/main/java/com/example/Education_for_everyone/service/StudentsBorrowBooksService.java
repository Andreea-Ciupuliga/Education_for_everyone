package com.example.Education_for_everyone.service;

import com.example.Education_for_everyone.exceptions.*;
import com.example.Education_for_everyone.models.*;
import com.example.Education_for_everyone.repository.*;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentsBorrowBooksService {

    private StudentRepository studentRepository;
    private BookRepository bookRepository;
    private StudentsBorrowBooksRepository studentsBorrowBooksRepository;


    @Autowired
    public StudentsBorrowBooksService(StudentRepository studentRepository, BookRepository bookRepository, StudentsBorrowBooksRepository studentsBorrowBooksRepository) {
        this.studentRepository = studentRepository;
        this.bookRepository = bookRepository;
        this.studentsBorrowBooksRepository = studentsBorrowBooksRepository;
    }

    @SneakyThrows
    public void borrowBook(String username,Long bookId)
    {
        Student student = studentRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("student not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(()->new BookNotFoundException("book not found"));

        //daca gasim perechea studentId si bookId inseamna ca are inchiriata cartea asta
        if(studentsBorrowBooksRepository.findBystudentIdAndbookId(student.getId(),bookId).isPresent())
        {
            throw new UserAlreadyExistException("student has already borrowed this book");
        }

        if(book.getAvailableCopies()<=0)
        {
            throw new NotEnoughAvailableCopiesException("Not Enough Available Copies");
        }

        StudentsBorrowBooks studentsBorrowBooks=StudentsBorrowBooks.builder()
                .student(Student.builder().id(student.getId()).build())
                .book(Book.builder().id(book.getId()).build())
                .title(book.getTitle())
                .studentFirstName(student.getFirstName())
                .studentLastName(student.getLastName()).build();

        studentsBorrowBooksRepository.save(studentsBorrowBooks);

        book.setAvailableCopies(book.getAvailableCopies()-1);
        bookRepository.save(book);

    }

    @SneakyThrows
    public void removeBookFromStudent(String username,Long bookId) {

        Student student = studentRepository.findByUsername(username).orElseThrow(()->new UserNotFoundException("student not found"));

        Book book = bookRepository.findById(bookId).orElseThrow(()->new BookNotFoundException("book not found"));

        //verificam ca exista perechea studentId si bookId
        StudentsBorrowBooks studentsBorrowBooks=studentsBorrowBooksRepository.findBystudentIdAndbookId(student.getId(),bookId).orElseThrow(()->new BookAndStudentDoNotMatchException("Book And Student Do Not Match"));

        studentsBorrowBooksRepository.delete(studentsBorrowBooks);

        book.setAvailableCopies(book.getAvailableCopies()+1);
        bookRepository.save(book);

    }

    //aratam toate cartile inchiriate de un student
    @SneakyThrows
    public List<String> booksBorrowedByStudent(Long studentId) {

        if(studentRepository.findById(studentId).isEmpty())
        {
            throw new UserNotFoundException("student not found");
        }

        return studentsBorrowBooksRepository.findTitleByStudentId(studentId);
    }

}
