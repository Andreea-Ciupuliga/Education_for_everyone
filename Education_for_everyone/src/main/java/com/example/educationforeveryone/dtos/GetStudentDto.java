package com.example.educationforeveryone.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetStudentDto {
    private String firstName;
    private String lastName;
    private String email;
}
