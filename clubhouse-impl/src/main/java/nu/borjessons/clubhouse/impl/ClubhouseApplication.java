package nu.borjessons.clubhouse.impl;

import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

import io.jsonwebtoken.security.Keys;
import nu.borjessons.clubhouse.impl.security.TokenStore;
import nu.borjessons.clubhouse.impl.security.util.JWTUtil;

@SpringBootApplication
public class ClubhouseApplication {
  public static void main(String[] args) {
    SpringApplication.run(ClubhouseApplication.class, args);
  }

  @Bean
  PasswordEncoder createPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  JWTUtil createJwtUtil(@Value("${token.secret}") String secret, @Value("${token.expiration}") String expirationTime) {
    final Key key = Keys.hmacShaKeyFor(secret.getBytes());
    long expirationMillis = Long.parseLong(expirationTime) * 1000;

    return new JWTUtil(expirationMillis, key);
  }

  @Bean
  TokenStore createTokenStore() {
    return new TokenStore(new ConcurrentHashMap<>());
  }

  @Bean
  ObjectMapper createObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    JavaTimeModule javaTimeModule = new JavaTimeModule();
    LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    LocalDateDeserializer localDateDeserializer = new LocalDateDeserializer(DateTimeFormatter.ISO_OFFSET_DATE);

    javaTimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
    javaTimeModule.addDeserializer(LocalDate.class, localDateDeserializer);
    objectMapper.registerModule(javaTimeModule);

    return objectMapper;
  }
}
