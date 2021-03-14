package nu.borjessons.clubhouse.impl.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

  @Value("${token.secret}")
  private String secret;

  @Value("${token.expiration}")
  private String expirationTime;

  private Key key;

  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(secret.getBytes());
  }

  public Claims getAllClaimsFromToken(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
  }

  public String getUsernameFromToken(String token) {
    return getAllClaimsFromToken(token).getSubject();
  }

  public Date getExpirationDateFromToken(String token) {
    return getAllClaimsFromToken(token).getExpiration();
  }

  private Boolean isTokenExpired(String token) {
    final Date expiration = getExpirationDateFromToken(token);
    return expiration.before(new Date());
  }

  public String generateToken(UserDetails user) {
    return doGenerateToken(user.getUsername());
  }

  private String doGenerateToken(String username) {
    Long expirationTimeLong = Long.parseLong(expirationTime);

    final Date createdDate = new Date();
    final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000);

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(createdDate)
        .setExpiration(expirationDate)
        .signWith(key)
        .compact();
  }

  public boolean validateToken(String token) {
    return !isTokenExpired(token);
  }
}
