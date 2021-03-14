package nu.borjessons.clubhouse.impl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ClubhouseApplication {

  public static void main(String[] args) {

    System.out.println("HELLO MOTHER FUCKER!!!!!!");
    SpringApplication.run(ClubhouseApplication.class, args);
  }

  @Bean
  public BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
