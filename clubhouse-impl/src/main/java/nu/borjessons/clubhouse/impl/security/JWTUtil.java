package nu.borjessons.clubhouse.impl.security;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JWTUtil {

  private final String expirationTime;
  private final Key key;

  public JWTUtil(String expirationTime, Key key) {
    this.expirationTime = expirationTime;
    this.key = key;
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

  boolean isExpired(String token) {
    return isTokenExpired(token);
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }
}
