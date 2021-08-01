package nu.borjessons.clubhouse.impl;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.jsonwebtoken.security.Keys;
import nu.borjessons.clubhouse.impl.security.JWTUtil;

@SpringBootApplication
public class ClubhouseApplication {
  public static void main(String[] args) {
    SpringApplication.run(ClubhouseApplication.class, args);
  }

  @Bean
  PasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  JWTUtil createJwtUtil(@Value("${token.secret}") String secret, @Value("${token.expiration}") String expirationTime) {
    final Key key = Keys.hmacShaKeyFor(secret.getBytes());
    long expirationMillis = Long.parseLong(expirationTime) * 1000;

    return new JWTUtil(expirationMillis, key);
  }
}
