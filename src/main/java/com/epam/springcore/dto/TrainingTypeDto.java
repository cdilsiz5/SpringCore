package com.epam.springcore.dto;

import com.epam.springcore.model.enums.Specialization;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TrainingTypeDto {
    private int id;
    private Specialization specialization;
}
