package nu.borjessons.clubhouse.impl.security;

import java.security.Key;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
class JWTUtil {

  @Value("${token.secret}")
  private String secret;

  @Value("${token.expiration}")
  private String expirationTime;

  private Key key;

  @PostConstruct
  void init() {
    key = Keys.hmacShaKeyFor(secret.getBytes());
  }

  Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  Date getExpirationDateFromToken(String token) {
    return getAllClaimsFromToken(token).getExpiration();
  }

  String doGenerateToken(String username) {
    long expirationTimeLong = Long.parseLong(expirationTime);

    final Date createdDate = new Date();
    final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000);

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(createdDate)
        .setExpiration(expirationDate)
        .signWith(key)
        .compact();
  }

  boolean validateToken(String token) {
    return !isTokenExpired(token);
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }
}
