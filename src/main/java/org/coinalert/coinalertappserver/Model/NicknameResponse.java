package org.coinalert.coinalertappserver.Model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class NicknameResponse {
    private String nickname;

    public NicknameResponse(String nickname) {
        this.nickname = nickname;
    }
}
