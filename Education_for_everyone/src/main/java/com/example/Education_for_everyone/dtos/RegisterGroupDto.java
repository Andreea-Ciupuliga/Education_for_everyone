package com.example.Education_for_everyone.dtos;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterGroupDto {

    private Long professorId;

    private String groupName;

    private String subject;

    private String yearOfStudy;

    private Long availableSeats;

    private Long totalSeats;
}
