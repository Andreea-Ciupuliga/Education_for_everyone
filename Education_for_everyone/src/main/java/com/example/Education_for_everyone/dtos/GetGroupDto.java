package com.example.Education_for_everyone.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetGroupDto {

    private String groupName;

    private String subject;

    private String yearOfStudy;

    private Long availableSeats;

}
