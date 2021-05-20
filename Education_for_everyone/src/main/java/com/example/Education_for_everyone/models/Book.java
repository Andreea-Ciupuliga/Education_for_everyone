package com.example.Education_for_everyone.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "available_copies")
    private Long availableCopies ;

    @Column(name = "total_copies")
    private Long totalCopies;

    @JsonIgnore
    @OneToMany(mappedBy = "book",orphanRemoval = true,cascade = {CascadeType.ALL})
    List<StudentsBorrowBooks> studentsBorrowBooks;
}
