package org.coinalert.coinalertappserver.Model;

import lombok.Getter;
import lombok.Setter;

// DTO for JWT response
@Getter
@Setter
public class JwtResponse {
    private String jwtToken;

    public JwtResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    // Getters and setters
}
