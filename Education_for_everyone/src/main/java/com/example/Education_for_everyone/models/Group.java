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
@Table(name = "grup")
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "subject")
    private String subject;

    @Column(name = "year_of_study")
    private String yearOfStudy;

    @Column(name = "available_seats")
    private Long availableSeats;

    @Column(name = "total_seats")
    private Long totalSeats;

    @JsonIgnore
    @OneToMany(mappedBy = "group",orphanRemoval = true,cascade = {CascadeType.ALL})
    List<GroupOfStudents> groupOfStudents;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private Professor professor;

}
