package com.munaf.airBnbApp.utils;

import com.munaf.airBnbApp.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwtSecretKey}")
    private String jwtSecretKey;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8)); // this is used to create SecretKey
    }

    public String generateAccessToken(User user) {
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("role", user.getRoles())
                .claim("tokenType", "ACCESS")
                .issuedAt(new Date())
                //.expiration(new Date(System.currentTimeMillis() + 1000 * 60 *10)) // 10 min TODO : use 10 min
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 3)) // 3 days // just for testing
                .signWith(getSecretKey())
                .compact();
    }


    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("tokenType", "REFRESH")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000L *60*60*24*30*6)) // 6 months
                .signWith(getSecretKey())
                .compact();
    }

    public Long getUserIdFromToken(String jwtToken) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();

        return Long.valueOf(claims.getSubject());
    }

    public String getTokenType(String jwtToken) {
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();

        return claims.get("tokenType", String.class);
    }

}
