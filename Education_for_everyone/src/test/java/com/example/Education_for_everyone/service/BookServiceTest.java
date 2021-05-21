package com.example.Education_for_everyone.service;
import com.example.Education_for_everyone.dtos.GetBookDto;
import com.example.Education_for_everyone.exceptions.BookNotFoundException;
import com.example.Education_for_everyone.models.Book;
import com.example.Education_for_everyone.repository.BookRepository;
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
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Autowired
    BookService bookService;

    @BeforeEach //avem adnotarea asta care se apeleaza inaintea fiecarui test
    void before() {
        bookService = new BookService(bookRepository);
    }

    @Test
    void getBookShouldSucceed() {
        //Arrange
        String title = "title";
        String author = "author";
        Long availableCopies = 5L;
        Long totalCopies= 5L;

        Book book = Book.builder().author(author).title(title).availableCopies(availableCopies).totalCopies(totalCopies).build();

        //Act
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book)); //definesc un comportament pt id-ul 1

        GetBookDto result = bookService.getBook(1L);


        //Assert
        Assertions.assertEquals(title,result.getTitle()); //rezultatul meu trebuie sa aiba title egal cu title pe care il definesc eu in Arrange
        Assertions.assertEquals(author,result.getAuthor());//rezultatul meu trebuie sa aiba author egal cu author pe care il definesc eu in Arrange
        Assertions.assertEquals(availableCopies,result.getAvailableCopies());//rezultatul meu trebuie sa aiba availableCopies egal cu availableCopies pe care il definesc eu in Arrange
        verify(bookRepository).findById(anyLong());
    }


    @Test
    void deleteBookShouldFailIfBookIsNotFound() {
        //Arrange
        String title = "title";
        String author = "author";
        Long availableCopies = 5L;
        Long totalCopies= 5L;

        Book book = Book.builder().author(author).title(title).availableCopies(availableCopies).totalCopies(totalCopies).build();

        //Act
        when(bookRepository.findById(book.getId())).thenReturn(Optional.empty());
        //cand cauta cartea dupa id returnez un optional emplty ptc nu am gasit o in baza de date


        //Assert
        //eu verific cazul in care cartea nu exista asa ca ma astept ca metoda mea sa arunce o exceptie deci rezultatul dorit ar fi sa arunce o exceptie
        Assertions.assertThrows(BookNotFoundException.class, () ->bookService.removeBook(book.getId()));


        verify(bookRepository, times(1)).findById(book.getId());//verific daca se face interactiunea cu findById doar o data
        verify(bookRepository,never()).delete(any(Book.class));//ma asigur ca nu s-a apelat delete

    }
}