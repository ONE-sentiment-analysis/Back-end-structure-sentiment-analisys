package br.com.one.sentiment_analysis.config;

import br.com.one.sentiment_analysis.model.user.ROLE;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

public class JwtUtil {
    private static final Key SECRET_KEY = Keys.hmacShaKeyFor(
            "minhaChaveSuperSecretaDePeloMenos32Caracteres!".getBytes()
    );

    public static String generateToken(String email, ROLE role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("authorities", Collections.singletonList(role.name()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
