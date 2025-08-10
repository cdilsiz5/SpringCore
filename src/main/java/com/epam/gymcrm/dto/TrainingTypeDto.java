package com.epam.gymcrm.dto;

import com.epam.gymcrm.model.enums.Specialization;
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
