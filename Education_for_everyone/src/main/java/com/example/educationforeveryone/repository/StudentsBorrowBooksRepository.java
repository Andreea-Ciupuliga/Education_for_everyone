package com.example.educationforeveryone.repository;

import com.example.educationforeveryone.models.StudentsBorrowBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentsBorrowBooksRepository extends JpaRepository<StudentsBorrowBooks, Long> {
    @Query("SELECT b from StudentsBorrowBooks b where b.student.id =:studentId AND b.book.id =:bookId")
    Optional<StudentsBorrowBooks> findByStudentIdAndBookId(@Param("studentId") Long studentId, @Param("bookId") Long bookId);

    @Query("SELECT b.title FROM StudentsBorrowBooks b where b.student.id=:studentId")
    List<String> findTitleByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT  b.book.id FROM StudentsBorrowBooks b WHERE b.student.id=:studentId")
    List<Long> findBookIdByStudentId(@Param("studentId") Long studentId);
}
