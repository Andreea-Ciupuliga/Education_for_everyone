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
@Table(name = "GRUP")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "GROUP_NAME")
    private String groupName;

    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "YEAR_OF_STUDY")
    private String yearOfStudy;

    @Column(name = "AVAILABLE_SEATS")
    private Long availableSeats;

    @Column(name = "TOTAL_SEATS")
    private Long totalSeats;

    @JsonIgnore
    @OneToMany(mappedBy = "group", orphanRemoval = true, cascade = {CascadeType.ALL})
    List<GroupOfStudents> groupOfStudents;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private Professor professor;
}
