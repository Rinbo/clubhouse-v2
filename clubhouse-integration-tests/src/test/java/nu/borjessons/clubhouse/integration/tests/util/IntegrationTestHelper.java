package nu.borjessons.clubhouse.integration.tests.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import nu.borjessons.clubhouse.impl.ClubhouseApplication;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.impl.controller.model.request.UserLoginRequestModel;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.security.SecurityConstants;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

  public static String loginUserWithHeader(String email, String password, String clubId) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL).path("/login");
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("clubId", clubId);
    HttpEntity<UserLoginRequestModel> httpEntity = new HttpEntity<>(new UserLoginRequestModel(email, password), httpHeaders);
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, String.class);
    HttpHeaders headers = response.getHeaders();
    List<String> authHeader = headers.get("Authorization");

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(authHeader);
    Assertions.assertTrue(authHeader.get(0).contains("Bearer"));
    return authHeader.get(0);
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
    HttpEntity<Void> entity = getVoidHttpEntity(token);

    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
    UserDTO userDTO = deserializeJsonBody(response.getBody(), UserDTO.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return userDTO;
  }

  private static <T> T deserializeJsonBody(String body, Class<T> clazz) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new ParameterNamesModule());
    mapper.setVisibility(FIELD, ANY);
    return mapper.readValue(body, clazz);
  }

  private static HttpEntity<Void> getVoidHttpEntity(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", token);
    headers.add("Accept", "application/json");
    return new HttpEntity<>(headers);
  }

  public static UserDTO registerClub(CreateClubModel createClubModel) throws JsonProcessingException {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL).path(SecurityConstants.CLUB_REGISTRATION_URL);
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<CreateClubModel> httpEntity = new HttpEntity<>(createClubModel);
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return deserializeJsonBody(response.getBody(), UserDTO.class);
  }

  public static UserDTO joinClub(String token, String clubId) throws JsonProcessingException {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL).path("/users/principal/join-club").queryParam("clubId", clubId);
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> httpEntity = getVoidHttpEntity(token);
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, httpEntity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return deserializeJsonBody(response.getBody(), UserDTO.class);
  }

  public static List<ClubDTO> getClubs() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL).path("/clubs/public");
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<ClubDTO[]> responseEntity = restTemplate.getForEntity(builder.toUriString(), ClubDTO[].class);
    ClubDTO[] clubs = responseEntity.getBody();
    Assertions.assertNotNull(clubs);
    return Arrays.stream(clubs).collect(Collectors.toList());
  }

  private static <T> T getList(String token, String uri, Class<T> type) {
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = getVoidHttpEntity(token);
    ResponseEntity<T> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, type);

    T arrayResponse = responseEntity.getBody();
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertNotNull(arrayResponse);
    return arrayResponse;
  }


  public static List<String> getRoles(String token, ClubDTO clubDTO) {
    UriComponentsBuilder builder = UriComponentsBuilder
        .fromHttpUrl(BASE_URL)
        .path("/users/club")
        .path("/" + clubDTO.getClubId())
        .path("/roles");
    
    return Arrays.stream(getList(token, builder.toUriString(), String[].class)).collect(Collectors.toList());
  }
}
