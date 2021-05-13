package com.example.Education_for_everyone.repository;


import com.example.Education_for_everyone.models.GroupOfStudents;
import com.example.Education_for_everyone.models.StudentsBorrowBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentsBorrowBooksRepository  extends JpaRepository<StudentsBorrowBooks, Long> {

    @Query("SELECT b from StudentsBorrowBooks b where b.student.id =:studentId AND b.book.id =:bookId")
    Optional<StudentsBorrowBooks> findBystudentIdAndbookId(@Param("studentId") Long studentId, @Param("bookId")Long bookId);

    @Query(value = "SELECT title FROM students_borrow_books where student_id=:studentId", nativeQuery = true)
    List<String> findTitleByStudentId(@Param("studentId") Long studentId);
}
