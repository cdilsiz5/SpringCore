package com.epam.gymcrm.request.trainer;

import com.epam.gymcrm.model.enums.Specialization;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateTrainerRequest {

    @NotNull(message = "Specialty must be specified")
    private Specialization specialization;
}
