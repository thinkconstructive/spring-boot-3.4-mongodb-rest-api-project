package com.thinkconstructive.book_store.service.impl;

import com.thinkconstructive.book_store.service.JWTService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTServiceImpl implements JWTService {

    private String secretKey = "";

    public JWTServiceImpl() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
        SecretKey secretKey1 = keyGenerator.generateKey();
        secretKey = Base64.getEncoder().encodeToString(secretKey1.getEncoded());
    }

    @Override
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60*60*30))
                .and()
                .signWith(getKey())
                .compact();
    }

    @Override
    public String extractUserName(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    @Override
    public boolean validateToken(String jwtToken, UserDetails userDetails) {
        final String userName = extractUserName(jwtToken);
        return (userName.equals(userDetails.getUsername()) &&
                !isTokenExpired(jwtToken));
    }

    private boolean isTokenExpired(String jwtToken)
    {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken)
    {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    private <T> T extractClaim(String jwtToken, Function<Claims, T> claimResolver)
    {
        final Claims claims = extractAllClaims(jwtToken);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken)
    {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload();
    }

    private SecretKey getKey()
    {
        byte[] keyValue = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyValue);
    }
}
