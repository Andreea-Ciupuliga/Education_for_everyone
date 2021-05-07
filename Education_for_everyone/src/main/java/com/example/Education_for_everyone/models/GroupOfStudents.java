package com.example.Education_for_everyone.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.Education_for_everyone.models.Student;
import javax.persistence.*;
import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "group_of_students")

public class GroupOfStudents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "student_first_name")
    private String studentFirstName;

    @Column(name = "student_last_name")
    private String studentLastName;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private Student student;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private Group group;



}
