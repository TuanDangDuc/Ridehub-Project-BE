package com.tuan.ridehub.service;

import com.tuan.ridehub.model.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {
    private String secretKey = "";

    JwtService() throws NoSuchAlgorithmException {
        KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256");
        SecretKey se = keygen.generateKey();
        secretKey = Base64.getEncoder().encodeToString(se.getEncoded());
    }

    public String generateToken(UserPrincipal userPrincipal) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userPrincipal.getAuthorities());
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(userPrincipal.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 30))
                .and()
                .signWith(getKey())
                .compact();
    }

    public SecretKey getKey() {
        byte[] keyByte = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyByte);
    }

    public String extractUsername(String token) {
        return ExtractClaims(token, Claims::getSubject);
    }

    public <T> T ExtractClaims(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(UserDetails userDetails, String token) {
        return (extractUsername(token).equals(userDetails.getUsername())) && !isExpired(token);
    }

    public boolean isExpired(String token) {
        return ExtractClaims(token, Claims::getExpiration).before(new Date());
    }
}
