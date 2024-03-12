package com.example.educationforeveryone.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetBookDto {
    private String title;
    private String author;
    private Long availableCopies;
}
