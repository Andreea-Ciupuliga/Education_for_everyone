package com.example.educationforeveryone.repository;

import com.example.educationforeveryone.models.StudentBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentBookRepository extends JpaRepository<StudentBook, Long> {
    @Query("SELECT b from StudentBook b where b.student.id =:studentId AND b.book.id =:bookId")
    Optional<StudentBook> findByStudentIdAndBookId(@Param("studentId") Long studentId, @Param("bookId") Long bookId);

    @Query("SELECT b.title FROM StudentBook b where b.student.id=:studentId")
    List<String> findTitleByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT b.book.id FROM StudentBook b WHERE b.student.id=:studentId")
    List<Long> findBookIdByStudentId(@Param("studentId") Long studentId);
}
