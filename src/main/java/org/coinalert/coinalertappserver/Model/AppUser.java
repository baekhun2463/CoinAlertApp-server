package org.coinalert.coinalertappserver.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "nick_name", unique = true)
    private String nickName;

    @Column(nullable = false)
    private String password;

    private String role;

    public AppUser(String email, String password) {
        this.nickName = nickName;
        this.email = email;
        this.password = password;
    }

}