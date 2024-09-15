package org.coinalert.coinalertappserver.DTO;

import lombok.Getter;
import lombok.Setter;

// DTO
@Getter
@Setter
public class JwtResponse {
    private String jwt;

    public JwtResponse(String jwt) {
        this.jwt = jwt;
    }

    // Getters and setters
}
