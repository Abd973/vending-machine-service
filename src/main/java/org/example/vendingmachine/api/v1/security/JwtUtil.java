package org.example.vendingmachine.api.v1.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.example.vendingmachine.api.v1.model.UserModel;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.util.Date;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Setter
@Getter
public class JwtUtil {

    private String secretKey;
    private long expirationMs;

    public String generateToken(UserModel user) {
        return Jwts
                .builder()
                .setSubject(user.getName())
                .claim("userId", user.getId())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public int getUserId(String token) {
        return getClaims(token).get("userId", Integer.class);
    }

    public boolean isValidToken(String token) {
        getClaims(token);
        return true;
    }

    protected Claims getClaims(String token) {
        return Jwts.parserBuilder()
                   .setSigningKey(secretKey.getBytes())
                   .build()
                   .parseClaimsJws(token)
                   .getBody();
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
