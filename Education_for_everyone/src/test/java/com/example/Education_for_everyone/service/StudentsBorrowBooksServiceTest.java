package com.example.Education_for_everyone.service;

import com.example.Education_for_everyone.dtos.RegisterStudentDto;
import com.example.Education_for_everyone.models.Book;
import com.example.Education_for_everyone.models.Student;
import com.example.Education_for_everyone.models.StudentsBorrowBooks;
import com.example.Education_for_everyone.repository.BookRepository;
import com.example.Education_for_everyone.repository.StudentRepository;
import com.example.Education_for_everyone.repository.StudentsBorrowBooksRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class StudentsBorrowBooksServiceTest {

    @Mock
    private StudentsBorrowBooksRepository studentsBorrowBooksRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private BookRepository bookRepository;

    @Autowired
    StudentsBorrowBooksService studentsBorrowBooksService;

    @BeforeEach //avem adnotarea asta care se apeleaza inaintea fiecarui test
    void before() {
        studentsBorrowBooksService = new StudentsBorrowBooksService(studentRepository,bookRepository,studentsBorrowBooksRepository);
    }

    @Test
    void borrowBookShouldSucceed() {

        //Arrange
        String username = "user1";
        Long bookId=1L;
        Long studentId=1L;


        Book book = Book.builder().id(bookId).build();
        book.setAvailableCopies(10L);
        Student student=Student.builder().id(studentId).username(username).build();

        //Act
        when(studentRepository.findByUsername(username)).thenReturn(Optional.of(student));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(studentsBorrowBooksRepository.findBystudentIdAndbookId(studentId,bookId)).thenReturn(Optional.empty());


        studentsBorrowBooksService.borrowBook(username,bookId);


        //Assert
        verify(studentRepository, times(1)).findByUsername(username);//verific daca se face interactiunea cu findByUsername doar o data
        verify(bookRepository, times(1)).findById(bookId);//verific daca se face interactiunea cu findByUsername doar o data
        verify(studentsBorrowBooksRepository, times(1)).save(any(StudentsBorrowBooks.class));//verific daca save-ul sa se apeleze o datea

    }

}