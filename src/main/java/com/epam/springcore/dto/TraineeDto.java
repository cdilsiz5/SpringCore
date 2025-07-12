package com.epam.springcore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TraineeDto {
    private String id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String address;
    private String username;
}
