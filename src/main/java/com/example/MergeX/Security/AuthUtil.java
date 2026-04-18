package com.example.MergeX.Security;

import com.example.MergeX.model.UserCredentials;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class AuthUtil {
    @Value("${jwt.secretKey}")
    private String jwtSecretKey;
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(UserCredentials userCredentials){
        return Jwts.builder()
                .subject(userCredentials.getEmail())   // see Fix #2
                .claim("userId", userCredentials.getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(getSignInKey())
                .compact();
    }
    public static String getCurrentUserEmail() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        return authentication.getName();
    }



}
