package com.springboot.tukserver;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class JwtConfig {

    private static final String SECRET = "my-super-secure-secret-key-which-is-very-long-and-random";

    @Bean
    public SecretKey jwtSecretKey() {
        byte[] keyBytes = SECRET.getBytes(StandardCharsets.UTF_8);
        SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);

        String encodedKey = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("✅ 서버에서 사용 중인 Secret Key: " + encodedKey);

        return secretKey;
    }
}
