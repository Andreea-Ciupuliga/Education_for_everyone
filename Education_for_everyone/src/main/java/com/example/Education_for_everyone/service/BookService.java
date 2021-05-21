package com.example.Education_for_everyone.service;

import com.example.Education_for_everyone.dtos.GetBookDto;
import com.example.Education_for_everyone.dtos.RegisterBookDto;
import com.example.Education_for_everyone.exceptions.BookAlreadyExistException;
import com.example.Education_for_everyone.exceptions.BookNotFoundException;
import com.example.Education_for_everyone.models.Book;
import com.example.Education_for_everyone.repository.BookRepository;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BookService {
    private BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    @SneakyThrows
    public void registerBook(RegisterBookDto registerBookDto)
    {

        if(bookRepository.findByTitle(registerBookDto.getTitle()).isPresent())
        {
            throw new BookAlreadyExistException("Book Already Exist");
        }

        Book book= Book.builder()
                .title(registerBookDto.getTitle())
                .author(registerBookDto.getAuthor())
                .availableCopies(registerBookDto.getTotalCopies()) //la inceput copiile disponibile ar trebui sa fie egale cu copiile totale deoarece inca nu a apucat nimeni sa inchirieze ceva
                .totalCopies(registerBookDto.getTotalCopies()).build();

        bookRepository.save(book);
    }


    @SneakyThrows
    public void removeBook(Long bookId)
    {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new BookNotFoundException("Book not found"));
        bookRepository.delete(book);
    }

    @SneakyThrows
    public GetBookDto getBook(Long bookId)
    {

        Book book = bookRepository.findById(bookId).orElseThrow(()->new BookNotFoundException("Book not found"));

        GetBookDto getBookDTO= GetBookDto.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .availableCopies(book.getAvailableCopies()).build();

        return getBookDTO;
    }

    @SneakyThrows
    public void putBook(Long bookId,RegisterBookDto newRegisterBookDto)
    {
        Book book = bookRepository.findById(bookId).orElseThrow(()->new BookNotFoundException("Book not found"));

        if(newRegisterBookDto.getTitle()!=null)
            book.setTitle(newRegisterBookDto.getTitle());

        if(newRegisterBookDto.getAuthor()!=null)
            book.setAuthor(newRegisterBookDto.getAuthor());

        if(newRegisterBookDto.getAvailableCopies()!=null)
            book.setAvailableCopies(newRegisterBookDto.getAvailableCopies());

        if(newRegisterBookDto.getTotalCopies()!=null)
            book.setTotalCopies(newRegisterBookDto.getTotalCopies());

        bookRepository.save(book);

    }


    @SneakyThrows
    public List<GetBookDto> getAllBooks() {

        if(bookRepository.findAllBooks().isEmpty())
            throw new BookNotFoundException("there are no books to display");

        //afisam toate cartile
        return bookRepository.findAllBooks();
    }

    @SneakyThrows
    public List<GetBookDto> getAllBooksByTitle(String title) {

        if(bookRepository.findAllByTitleContains(title).isEmpty())
        {
            throw new BookNotFoundException("Title Not Found");
        }

        return bookRepository.findAllByTitleContains(title);
    }

    @SneakyThrows
    public List<GetBookDto> getAllBooksByAuthor(String author) {

        if(bookRepository.findAllByAuthorContains(author).isEmpty())
        {
            throw new BookNotFoundException("Author Not Found");
        }

        return bookRepository.findAllByAuthorContains(author);
    }

}
