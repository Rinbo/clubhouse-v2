package nu.borjessons.clubhouse.integration.tests.util;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import nu.borjessons.clubhouse.impl.ClubhouseApplication;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.UserLoginRequestModel;
import nu.borjessons.clubhouse.impl.security.SecurityConstants;

public class IntegrationTestHelper {
  private static final String BASE_URL = "http://localhost:8081";

  public static ClubDTO getClub(String clubId, String token) {
    final String uri = getUriBuilder("/clubs/{clubId}").buildAndExpand(clubId).toUriString();
    final ResponseEntity<ClubDTO> response = getResponse(uri, token, ClubDTO.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static List<UserDTO> getClubUsers(String clubId, String token) throws JsonProcessingException {
    final String uri = getUriBuilder("/clubs/{clubId}/users").buildAndExpand(clubId).toUriString();
    final ResponseEntity<String> response = getResponse(uri, token, String.class);
    final UserDTO[] userDTOs = deserializeJsonBody(response.getBody(), UserDTO[].class);
    return Arrays.stream(userDTOs).collect(Collectors.toList());
  }

  public static List<ClubDTO> getClubs() {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL).path("/clubs/public");
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<ClubDTO[]> responseEntity = restTemplate.getForEntity(builder.toUriString(), ClubDTO[].class);
    ClubDTO[] clubs = responseEntity.getBody();
    Assertions.assertNotNull(clubs);
    return Arrays.stream(clubs).collect(Collectors.toList());
  }

  public static List<String> getRoles(String token, ClubDTO clubDTO) {
    UriComponentsBuilder builder = UriComponentsBuilder
        .fromHttpUrl(BASE_URL)
        .path("/users/club")
        .path("/" + clubDTO.getClubId())
        .path("/roles");

    return Arrays.stream(getList(token, builder.toUriString(), String[].class)).collect(Collectors.toList());
  }

  public static UserDTO getSelf(String token) throws JsonProcessingException {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL).path("/principal");
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = getVoidHttpEntity(token);

    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
    UserDTO userDTO = deserializeJsonBody(response.getBody(), UserDTO.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return userDTO;
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

  public static String loginUserWithHeader(String email, String password, String clubId) {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL).path("/login");
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("clubId", clubId);
    HttpEntity<UserLoginRequestModel> httpEntity = new HttpEntity<>(new UserLoginRequestModel(password, email), httpHeaders);
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, String.class);
    HttpHeaders headers = response.getHeaders();
    List<String> authHeader = headers.get("Authorization");

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(authHeader);
    Assertions.assertTrue(authHeader.get(0).contains("Bearer"));
    return authHeader.get(0);
  }

  public static UserDTO registerClub(CreateClubModel createClubModel) throws JsonProcessingException {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL).path(SecurityConstants.CLUB_REGISTRATION_URL);
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<CreateClubModel> httpEntity = new HttpEntity<>(createClubModel);
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return deserializeJsonBody(response.getBody(), UserDTO.class);
  }

  public static ConfigurableApplicationContext runSpringApplication() {
    ConfigurableEnvironment environment = new StandardEnvironment();
    MutablePropertySources propertySources = environment.getPropertySources();
    Map<String, Object> myMap = new HashMap<>();
    myMap.put("token.secret", "öalkdsjföasldfjaösldkfjaösdlkfjaösdlfkjasdöflkj");
    propertySources.addFirst(new MapPropertySource("CUSTOM_PROPS", myMap));

    return new SpringApplicationBuilder()
        .profiles("test")
        .environment(environment)
        .sources(ClubhouseApplication.class)
        .run();
  }

  public static UserDTO updateSelf(String token, UpdateUserModel updateUserModel) throws JsonProcessingException {
    UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BASE_URL).path("/users/principal");
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = getHttpHeaders(token);
    HttpEntity<UpdateUserModel> entity = new HttpEntity<>(updateUserModel, headers);
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, entity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return deserializeJsonBody(response.getBody(), UserDTO.class);
  }

  public static UriComponentsBuilder getUriBuilder(String path) {
    return UriComponentsBuilder.fromHttpUrl(BASE_URL).path(path);
  }

  public static <T, U> ResponseEntity<U> postRequest(String uri, String token, T requestObject, Class<U> returnType) {
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<T> entity = getHttpEntity(token, requestObject);
    return restTemplate.exchange(uri, HttpMethod.POST, entity, returnType);
  }

  public static <T> T deserializeJsonBody(String body, Class<T> clazz) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new ParameterNamesModule());
    mapper.setVisibility(FIELD, ANY);
    return mapper.readValue(body, clazz);
  }

  private static HttpHeaders getHttpHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", token);
    headers.add("Accept", "application/json");
    return headers;
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

  private static <T> ResponseEntity<T> getResponse(String uri, String token, Class<T> clazz) {
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = getVoidHttpEntity(token);
    return restTemplate.exchange(uri, HttpMethod.GET, entity, clazz);
  }

  private static <T> HttpEntity<T> getHttpEntity(String token, T requestObject) {
    HttpHeaders headers = getHttpHeaders(token);
    return new HttpEntity<T>(requestObject, headers);
  }

  private static HttpEntity<Void> getVoidHttpEntity(String token) {
    HttpHeaders headers = getHttpHeaders(token);
    return new HttpEntity<>(headers);
  }
}
