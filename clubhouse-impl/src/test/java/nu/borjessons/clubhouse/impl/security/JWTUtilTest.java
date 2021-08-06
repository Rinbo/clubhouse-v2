package nu.borjessons.clubhouse.impl.security;

import java.security.Key;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.Keys;
import nu.borjessons.clubhouse.impl.security.util.JWTUtil;

class JWTUtilTest {
  private static final Key KEY = Keys.hmacShaKeyFor("alsöjdfghaölvjpnöafödkvjandfövajnskdövlkanvöalgfhijnaöfljkvnaöflkjvn".getBytes());
  private static final String USERNAME = "robin@ex.com";

  @Test
  void expirationOverDueTest() {
    final long expirationMillis = -1000;
    final JWTUtil jwtUtil = new JWTUtil(expirationMillis, KEY);
    final String token = jwtUtil.doGenerateToken(USERNAME);
    Assertions.assertThrows(ExpiredJwtException.class, () -> jwtUtil.getAllClaimsFromToken(token), "");
  }

  @Test
  void tokenTest() {
    final long expirationMillis = 10000;
    final JWTUtil jwtUtil = new JWTUtil(expirationMillis, KEY);
    final String token = jwtUtil.doGenerateToken(USERNAME);
    final Claims claims = jwtUtil.getAllClaimsFromToken(token);

    Assertions.assertEquals(USERNAME, claims.getSubject());
  }
}