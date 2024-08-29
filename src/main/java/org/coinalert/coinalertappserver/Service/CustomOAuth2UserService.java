package org.coinalert.coinalertappserver.Service;

import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.coinalert.coinalertappserver.Model.OAuth2Member;
import org.coinalert.coinalertappserver.Repository.OAuth2MemberRepository;
import org.coinalert.coinalertappserver.Util.OAuth2UserInfo;
import org.coinalert.coinalertappserver.Util.PrincipalDetails;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final OAuth2MemberRepository oAuth2MemberRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuth2UserInfo oAuth2UserInfo = null;
        try {
            oAuth2UserInfo = OAuth2UserInfo.of(registrationId, oAuth2UserAttributes);
        } catch (AuthException e) {
            throw new RuntimeException(e);
        }

        OAuth2Member oauth2Member = getOrSave(oAuth2UserInfo);

        return new PrincipalDetails(oauth2Member, oAuth2UserAttributes, userNameAttributeName);
    }

    private OAuth2Member getOrSave(OAuth2UserInfo oAuth2UserInfo) {
        OAuth2Member oauth2Member = oAuth2MemberRepository.findByEmail(oAuth2UserInfo.email())
                .orElseGet(oAuth2UserInfo::toEntity);
        return oAuth2MemberRepository.save(oauth2Member);
    }
}
