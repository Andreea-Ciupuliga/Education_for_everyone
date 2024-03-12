package com.example.educationforeveryone.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "BOOK")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "AUTHOR")
    private String author;

    @Column(name = "AVAILABLE_COPIES")
    private Long availableCopies;

    @Column(name = "TOTAL_COPIES")
    private Long totalCopies;

    @JsonIgnore
    @OneToMany(mappedBy = "book", orphanRemoval = true, cascade = {CascadeType.ALL})
    List<StudentsBorrowBooks> studentsBorrowBooks;
}
