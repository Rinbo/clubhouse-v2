package nu.borjessons.clubhouse.impl.security.util;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JWTUtil {
  private final long expirationMillis;
  private final Key key;

  public JWTUtil(long expirationMillis, Key key) {
    this.expirationMillis = expirationMillis;
    this.key = key;
  }

  public Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  public String doGenerateToken(String username) {
    final Date createdDate = new Date();
    final Date expirationDate = new Date(createdDate.getTime() + expirationMillis);

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(createdDate)
        .setExpiration(expirationDate)
        .signWith(key)
        .compact();
  }
}
