package com.example.Education_for_everyone.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterProfessorDto {
    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String username;

    private String subject;
}
