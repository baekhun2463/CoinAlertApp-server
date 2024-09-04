package org.coinalert.coinalertappserver.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDTO {
    private String email;
    private String password;
}
