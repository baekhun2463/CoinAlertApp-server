package org.coinalert.coinalertappserver.Model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GitHubUserDTO {
    private String id;
    private String login;
    private String name;
    private String email;
    private String avatar_url;
}
