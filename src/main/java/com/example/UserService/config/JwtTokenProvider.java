package com.example.UserService.config;

import com.example.UserService.dto.JWTAuthResponse;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    // 토큰의 암호화/복호화를 위한 secret key
    @Value("${secretKey}")
    private String secretKey;

    public static final String BEARER = "Bearer";

    // Refresh Token 유효 기간 14일 (ms 단위)
    private final Long REFRESH_TOKEN_VALID_TIME = 14 * 1440 * 60 * 1000L;

    // Access Token 유효 기간 15분
    private final Long ACCESS_TOKEN_VALID_TIME = 15 * 60 * 1000L;

    private final MyUserDetailsService userDetailsService;

    // 의존성 주입이 완료된 후에 실행되는 메소드, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // generate JWT token
    public JWTAuthResponse generateToken(String email, Authentication authentication) {
        String username = authentication.getName();

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("username", username);

        Date currentDate = new Date();
        Date accessTokenExpireDate = new Date(currentDate.getTime() + ACCESS_TOKEN_VALID_TIME);
        Date refreshTokenExpireDate = new Date(currentDate.getTime() + REFRESH_TOKEN_VALID_TIME);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(currentDate)
                .setExpiration(accessTokenExpireDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setExpiration(refreshTokenExpireDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        JWTAuthResponse response = new JWTAuthResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType(BEARER);
        response.setAccessTokenExpireDate(ACCESS_TOKEN_VALID_TIME);
        return response;
    }

    public String getEmail(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }

    // JWT 토큰을 복호화하여 토큰에 들어있는 사용자 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", new ArrayList<>());
    }

    // Request의 Header로부터 토큰 값 조회
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }

    // 토큰의 유효성 검증
    public boolean validateToken(String jwtToken) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return true;
        } catch (SecurityException e) {
            throw new JwtException("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            throw new JwtException("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            throw new JwtException("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            throw new JwtException("JWT token compact of handler are invalid.");
        }

    }
}