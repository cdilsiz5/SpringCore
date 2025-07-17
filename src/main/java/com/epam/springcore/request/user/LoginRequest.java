package com.epam.springcore.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {


    @NotBlank(message ="Username Cannot Be Null")
    private String username;

    @NotBlank(message ="Password Cannot Be Null")
    private String password;
}
