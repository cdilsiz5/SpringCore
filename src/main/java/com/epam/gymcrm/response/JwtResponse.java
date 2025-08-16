package com.epam.gymcrm.response;

import com.epam.gymcrm.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {
    private String accessToken;
    @Builder.Default
    private String type = "Bearer";
    private UserDto userDto;

    public JwtResponse(String accessToken, UserDto userDto) {
        this.accessToken = accessToken;
        this.userDto = userDto;
    }
}