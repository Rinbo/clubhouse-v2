package nu.borjessons.clubhouse.integration.tests.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import nu.borjessons.clubhouse.impl.ClubhouseApplication;
import nu.borjessons.clubhouse.impl.controller.model.request.UserLoginRequestModel;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
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

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

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

  public static UserDTO getSelf(String token) throws JsonProcessingException {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL).path("/users/principal");
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = getVoidHttpEntity(EmbeddedDataLoader.defaultClubId, token);

    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
    UserDTO userDTO = deserializeUserDTO(response.getBody(), UserDTO.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return userDTO;
  }

  private static <T> T deserializeUserDTO(String body, Class<T> clazz) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new ParameterNamesModule());
    mapper.setVisibility(FIELD, ANY);
    return mapper.readValue(body, clazz);
  }

  private static HttpEntity<Void> getVoidHttpEntity(String clubId, String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("ClubId", clubId);
    headers.add("Authorization", token);
    headers.add("Accept", "application/json");
    return new HttpEntity<>(headers);
  }
}
