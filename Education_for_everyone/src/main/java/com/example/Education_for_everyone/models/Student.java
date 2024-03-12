package com.example.Education_for_everyone.models;

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
@Table(name = "STUDENT")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "USERNAME")
    private String username;

    @JsonIgnore
    @OneToMany(mappedBy = "student", orphanRemoval = true, cascade = {CascadeType.ALL})
    List<GroupOfStudents> groupOfStudents;

    @JsonIgnore
    @OneToMany(mappedBy = "student", orphanRemoval = true, cascade = {CascadeType.ALL})
    List<StudentsBorrowBooks> studentsBorrowBooks;

    @JsonIgnore
    @OneToMany(mappedBy = "student", orphanRemoval = true, cascade = {CascadeType.ALL})
    List<Grade> grade;
}
