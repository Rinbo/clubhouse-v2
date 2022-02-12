package nu.borjessons.clubhouse.impl;

import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.deserializer.AnnouncementIdDeserializer;
import nu.borjessons.clubhouse.impl.dto.deserializer.ImageTokenIdDeserializer;
import nu.borjessons.clubhouse.impl.dto.deserializer.UserIdDeserializer;
import nu.borjessons.clubhouse.impl.dto.serializer.AnnouncementIdSerializer;
import nu.borjessons.clubhouse.impl.dto.serializer.ImageTokenIdSerializer;
import nu.borjessons.clubhouse.impl.dto.serializer.UserIdSerializer;
import nu.borjessons.clubhouse.impl.repository.FileImageRepository;
import nu.borjessons.clubhouse.impl.repository.ImageRepository;
import nu.borjessons.clubhouse.impl.security.TokenStore;
import nu.borjessons.clubhouse.impl.security.util.JWTUtil;

@Slf4j
@SpringBootApplication
public class ClubhouseApplication {
  public static final String APP_NAME = "clubhouse";

  public static void main(String[] args) {
    SpringApplication.run(ClubhouseApplication.class, args);
  }

  private static Path createClubhouseDirectory(Path base) throws IOException {
    Path clubhouseDirectory = base.resolve(APP_NAME);
    if (Files.isDirectory(clubhouseDirectory))
      return clubhouseDirectory;

    log.info("creating directory for image upload: {}", clubhouseDirectory);
    return Files.createDirectory(clubhouseDirectory);
  }

  private static Module createIdModule() {
    SimpleModule simpleModule = new SimpleModule();

    simpleModule.addSerializer(UserId.class, UserIdSerializer.INSTANCE);
    simpleModule.addSerializer(ImageTokenId.class, ImageTokenIdSerializer.INSTANCE);
    simpleModule.addSerializer(AnnouncementId.class, AnnouncementIdSerializer.INSTANCE);

    simpleModule.addDeserializer(UserId.class, UserIdDeserializer.INSTANCE);
    simpleModule.addDeserializer(ImageTokenId.class, ImageTokenIdDeserializer.INSTANCE);
    simpleModule.addDeserializer(AnnouncementId.class, AnnouncementIdDeserializer.INSTANCE);

    return simpleModule;
  }

  private static DateTimeFormatter createTimeFormatter() {
    return new DateTimeFormatterBuilder()
        .appendValue(HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(MINUTE_OF_HOUR, 2)
        .toFormatter();
  }

  @Bean
  ImageRepository createImageRepository() {
    Path tmpDir = Paths.get(System.getProperty("java.io.tmpdir"));
    try {
      return new FileImageRepository(createClubhouseDirectory(tmpDir));
    } catch (IOException e) {
      log.error("Could not create directory for images, defaulting to system temp directory");
      return new FileImageRepository(tmpDir);
    }
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
    LocalTimeDeserializer localTimeDeserializer = new LocalTimeDeserializer(DateTimeFormatter.ISO_TIME);
    LocalTimeSerializer localTimeSerializer = new LocalTimeSerializer(createTimeFormatter());

    javaTimeModule.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
    javaTimeModule.addDeserializer(LocalDate.class, localDateDeserializer);
    javaTimeModule.addDeserializer(LocalTime.class, localTimeDeserializer);

    javaTimeModule.addSerializer(LocalTime.class, localTimeSerializer);
    objectMapper.registerModules(javaTimeModule, createIdModule());

    return objectMapper;
  }
}
