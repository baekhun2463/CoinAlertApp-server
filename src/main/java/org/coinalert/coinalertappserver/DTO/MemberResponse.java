package org.coinalert.coinalertappserver.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResponse {
    private String nickname;
    private Long memberId;

    public MemberResponse(String nickname, Long memberId) {
        this.memberId = memberId;
        this.nickname = nickname;
    }
}
