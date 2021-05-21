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
@Table(name = "homework")
public class Homework {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task")
    private String task;

    @Column(name = "deadline")
    private String deadline;

    @Column(name = "points")
    private Long points;

    @JsonIgnore
    @OneToMany(mappedBy = "homework",orphanRemoval = true,cascade = {CascadeType.ALL})
    List<Grade> grade;
}
