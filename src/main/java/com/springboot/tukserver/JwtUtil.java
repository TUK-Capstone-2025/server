package com.springboot.tukserver;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1시간 (밀리초)

    // ✅ 생성자를 통해 SecretKey 주입받기
    public JwtUtil(SecretKey secretKey) {
        // ✅ Base64로 인코딩된 Secret Key 가져오기 (application.yml에서 주입 가능)
        String encodedSecretKey = "9gPNa/xSj64GQOSvY92MjOUdW3C9xLix1m2yNlU1w8Vv3fKzP1KAewsaO5stGVrw0yle5oJwlzxkoPRCQos2Xg==";
        byte[] decodedKey = Base64.getDecoder().decode(encodedSecretKey);
        this.secretKey = Keys.hmacShaKeyFor(decodedKey); // ✅ SecretKey 생성


        System.out.println("✅ JwtUtil Secret Key: " + Base64.getEncoder().encodeToString(secretKey.getEncoded()));
    }

    // ✅ JWT 토큰 생성
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS384)
                .compact();
    }

    // ✅ JWT 토큰에서 사용자 정보 추출
    public String extractUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    // ✅ JWT 토큰에서 Claims(사용자 정보) 추출
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


    // ✅ JWT 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

