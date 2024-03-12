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
@Table(name = "HOMEWORK")
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TASK")
    private String task;

    @Column(name = "DEADLINE")
    private String deadline;

    @Column(name = "POINTS")
    private Long points;

    @JsonIgnore
    @OneToMany(mappedBy = "homework", orphanRemoval = true, cascade = {CascadeType.ALL})
    List<Grade> grade;
}
