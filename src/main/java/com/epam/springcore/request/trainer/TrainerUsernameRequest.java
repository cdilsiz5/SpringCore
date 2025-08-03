package com.epam.springcore.request.trainer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Trainer identifier for updating trainee's trainer list")
public class TrainerUsernameRequest {

    @NotBlank
    @Schema(description = "Trainer username", example = "john.smith")
    private String username;
}
