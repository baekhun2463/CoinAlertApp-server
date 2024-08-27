package org.coinalert.coinalertappserver.Controller;

import jakarta.servlet.http.HttpServletResponse;
import org.coinalert.coinalertappserver.Model.JwtResponse;
import org.coinalert.coinalertappserver.Util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class GithubLoginController {


    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;


    @GetMapping("/github-login")
    public String GitHubLogin(OAuth2AuthenticationToken token) {
        System.out.println(token.getPrincipal().getAttributes());
        return "Hello!";
    }


}
