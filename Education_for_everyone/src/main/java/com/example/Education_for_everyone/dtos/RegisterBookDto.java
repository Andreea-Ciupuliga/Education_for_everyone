package com.example.Education_for_everyone.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterBookDto {

    private String title;

    private String author;

    private Long availableCopies;

    private Long totalCopies;
}
