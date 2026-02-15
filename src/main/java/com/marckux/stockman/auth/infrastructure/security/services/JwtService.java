package com.marckux.stockman.auth.infrastructure.security.services;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.marckux.stockman.auth.domain.model.User;
import com.marckux.stockman.auth.domain.ports.out.TokenProviderPort;
import com.marckux.stockman.auth.infrastructure.security.mappers.SecurityUserMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtService implements TokenProviderPort {
  @Value("${jwt.secret}")
  private String jwtSecret;
  @Value("${jwt.expiration}")
  private int jwtExpiration;

  private final SecurityUserMapper mapper;

  private SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public String generateToken(User user) {
    return generateToken(mapper.toUserDetails(user));

  }

  public String generateToken(UserDetails user) {
    return Jwts.builder()
        .subject(user.getUsername())
        .signWith(getSecretKey(), Jwts.SIG.HS256)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
        .compact();
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSecretKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public boolean isTokenValid(String token, UserDetails user) {
    final String username = extractUsername(token);
    return username.equals(user.getUsername()) && !isTokenExpired(token);
  }

}
