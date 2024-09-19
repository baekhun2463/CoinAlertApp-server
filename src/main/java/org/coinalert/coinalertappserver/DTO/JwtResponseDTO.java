package org.coinalert.coinalertappserver.DTO;

import lombok.Getter;
import lombok.Setter;

// DTO
@Getter
@Setter
public class JwtResponseDTO {
    private String jwt;

    public JwtResponseDTO(String jwt) {
        this.jwt = jwt;
    }

    // Getters and setters
}
