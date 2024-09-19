package org.coinalert.coinalertappserver.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberResponseDTO {
    private String nickname;
    private Long memberId;

    public MemberResponseDTO(String nickname, Long memberId) {
        this.memberId = memberId;
        this.nickname = nickname;
    }
}
