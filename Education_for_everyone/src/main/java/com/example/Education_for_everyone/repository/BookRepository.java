package com.example.Education_for_everyone.repository;

import com.example.Education_for_everyone.dtos.GetBookDto;
import com.example.Education_for_everyone.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByTitle(String title);

    @Query("SELECT new com.example.Education_for_everyone.dtos.GetBookDto(b.title, b.author, b.availableCopies) FROM Book b WHERE b.title LIKE %:title%")
    List<GetBookDto> findAllByTitleContains(@Param("title") String title);

    @Query("SELECT new com.example.Education_for_everyone.dtos.GetBookDto(b.title, b.author, b.availableCopies) FROM Book b WHERE b.author LIKE %:author%")
    List<GetBookDto> findAllByAuthorContains(@Param("author") String author);

    @Query("SELECT new com.example.Education_for_everyone.dtos.GetBookDto(b.title, b.author, b.availableCopies) FROM Book b")
    List<GetBookDto> findAllBooks();

}
