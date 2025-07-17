package com.epam.springcore.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerDto {
    private String specialization;
    private UserDto user;
}
