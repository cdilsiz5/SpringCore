package com.epam.springcore.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainerDto {
    private Long id;
    private String specialization;
    private UserDto user;
}
