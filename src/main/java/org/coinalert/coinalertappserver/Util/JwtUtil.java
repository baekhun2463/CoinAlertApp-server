package org.coinalert.coinalertappserver.Util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    @Value("${jwt.issuer}")
    private String jwtIssuer;
    private final long jwtAccessTokenExpirationInMs = 3600000; // 1 hour
    private final long jwtRefreshTokenExpirationInMs = 86400000; // 24 hours

    // JWT Access Token 생성
    public String generateAccessToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + jwtAccessTokenExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // JWT Refresh Token 생성
    public String generateRefreshToken(Authentication authentication, String accessToken) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiredDate = new Date(now.getTime() + jwtRefreshTokenExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(jwtIssuer)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // 기존 JWT 토큰 생성 메서드 (OAuth2용)
    public String generateTokenOauth2(String accessToken) {
        // JWT 토큰 생성 로직
        return Jwts.builder()
                .setSubject(accessToken)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtAccessTokenExpirationInMs))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();
    }

    // JWT 토큰에서 사용자명 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // JWT 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
