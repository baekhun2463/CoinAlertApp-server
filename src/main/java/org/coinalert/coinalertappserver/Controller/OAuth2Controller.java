package org.coinalert.coinalertappserver.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class OAuth2Controller {

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String redirectUri;

    private final AuthenticationSuccessHandler successHandler;

    @GetMapping("/github-login")
    public void redirectToGitHub(HttpServletResponse response) throws IOException {
        // GitHub 인증 페이지로 리디렉션
        String githubAuthUrl = "https://github.com/login/oauth/authorize?client_id=" + clientId +
                "&redirect_uri=" + redirectUri;

        response.sendRedirect(githubAuthUrl);
    }

    @GetMapping("/github-callback")
    public void handleGitHubCallback(HttpServletResponse response, OAuth2AuthenticationToken authentication) throws IOException, ServletException {
        // 인증 성공 핸들러를 통해 JWT 생성 및 Swift 앱으로 리디렉션
        successHandler.onAuthenticationSuccess(null, response, authentication);
    }
}