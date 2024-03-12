package com.example.Education_for_everyone.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "GRADE")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "STUDENT_FIRST_NAME")
    private String studentFirstName;

    @Column(name = "STUDENT_LAST_NAME")
    private String studentLastName;

    @Column(name = "SCORE")
    private Long score;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private Student student;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private Homework homework;
}
