package nu.borjessons.clubhouse.integration.tests.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nu.borjessons.clubhouse.impl.ClubhouseApplication;
import nu.borjessons.clubhouse.impl.controller.model.request.UserLoginRequestModel;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class IntegrationTestHelper {
  private static final String BASE_URL = "http://localhost:8081";

  public static String getJsonStringFromFile(String filename) throws IOException {
    try (Reader reader = Files.newBufferedReader(Paths.get("/src/test/resources", filename))) {
      JsonNode jsonNode = new ObjectMapper().readTree(reader);
      return jsonNode.toString();
    }
  }

  public static ConfigurableApplicationContext runSpringApplication() {
    return new SpringApplicationBuilder()
        .profiles("test")
        .sources(ClubhouseApplication.class)
        .run();
  }

  public static String loginUser(String email, String password) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL).path("/login");
    RestTemplate restTemplate = new RestTemplate();

    HttpEntity<UserLoginRequestModel> httpEntity = new HttpEntity<>(new UserLoginRequestModel(email, password));
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, String.class);
    HttpHeaders headers = response.getHeaders();
    List<String> authHeader = headers.get("Authorization");

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(authHeader);
    Assertions.assertTrue(authHeader.get(0).contains("Bearer"));
    return authHeader.get(0);
  }
}
