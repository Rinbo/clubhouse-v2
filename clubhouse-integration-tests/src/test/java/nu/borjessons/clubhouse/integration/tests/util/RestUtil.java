package nu.borjessons.clubhouse.integration.tests.util;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.deserializer.AnnouncementIdDeserializer;
import nu.borjessons.clubhouse.impl.dto.deserializer.ImageTokenIdDeserializer;
import nu.borjessons.clubhouse.impl.dto.deserializer.TeamPostIdDeserializer;
import nu.borjessons.clubhouse.impl.dto.deserializer.UserIdDeserializer;
import nu.borjessons.clubhouse.impl.dto.serializer.AnnouncementIdSerializer;
import nu.borjessons.clubhouse.impl.dto.serializer.ImageTokenIdSerializer;
import nu.borjessons.clubhouse.impl.dto.serializer.TeamPostIdSerializer;
import nu.borjessons.clubhouse.impl.dto.serializer.UserIdSerializer;

public class RestUtil {
  public static final String BASE_URL = "http://localhost:8081";

  public static RestTemplate createRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    restTemplate.setErrorHandler(NoOpResponseErrorHandler.INSTANCE);
    return restTemplate;
  }

  public static <U> ResponseEntity<U> deleteRequest(String uri, String token, Class<U> returnType) {
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = getVoidHttpEntity(token);
    return restTemplate.exchange(uri, HttpMethod.DELETE, entity, returnType);
  }

  public static <T> T deserializeJsonBody(String body, Class<T> clazz) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();

    JavaTimeModule javaTimeModule = new JavaTimeModule();
    LocalTimeDeserializer localTimeDeserializer = new LocalTimeDeserializer(DateTimeFormatter.ISO_TIME);
    javaTimeModule.addDeserializer(LocalTime.class, localTimeDeserializer);

    mapper.registerModules(new ParameterNamesModule(), javaTimeModule, createIdModule());
    mapper.setVisibility(FIELD, ANY);
    return mapper.readValue(body, clazz);
  }

  public static <T> HttpEntity<T> getHttpEntity(String token, T requestObject) {
    HttpHeaders headers = getHttpHeaders(token);
    return new HttpEntity<T>(requestObject, headers);
  }

  public static HttpHeaders getHttpHeaders(String token) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Cookie", token);
    headers.add("Accept", "application/json");
    return headers;
  }

  public static <T> T getList(String token, String uri, Class<T> type) {
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = getVoidHttpEntity(token);
    ResponseEntity<T> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, type);

    T arrayResponse = responseEntity.getBody();
    Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    Assertions.assertNotNull(arrayResponse);
    return arrayResponse;
  }

  public static <T> ResponseEntity<T> getRequest(String uri, String token, Class<T> responseType) {
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = getVoidHttpEntity(token);
    return restTemplate.exchange(uri, HttpMethod.GET, entity, responseType);
  }

  public static UriComponentsBuilder getUriBuilder(String path) {
    return UriComponentsBuilder.fromHttpUrl(BASE_URL).path(path);
  }

  public static HttpEntity<Void> getVoidHttpEntity(String token) {
    HttpHeaders headers = getHttpHeaders(token);
    return new HttpEntity<>(headers);
  }

  public static <T, U> ResponseEntity<U> postRequest(String uri, String token, T requestObject, Class<U> returnType) {
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<T> entity = getHttpEntity(token, requestObject);
    return restTemplate.exchange(uri, HttpMethod.POST, entity, returnType);
  }

  public static <T, U> ResponseEntity<U> postSerializedRequest(String uri, String token, T requestObject, Class<U> returnType) throws JsonProcessingException {
    RestTemplate restTemplate = new RestTemplate();
    String serializedRequestBody = getObjectMapper().writeValueAsString(requestObject);
    HttpEntity<String> entity = getHttpEntity(token, serializedRequestBody);
    return restTemplate.exchange(uri, HttpMethod.POST, entity, returnType);
  }

  public static <T, U> ResponseEntity<U> putRequest(String uri, String token, T requestObject, Class<U> returnType) {
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<T> entity = getHttpEntity(token, requestObject);
    return restTemplate.exchange(uri, HttpMethod.PUT, entity, returnType);
  }

  public static <T, U> ResponseEntity<U> putRequest(String uri, String token, Class<U> returnType) {
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = getVoidHttpEntity(token);
    return restTemplate.exchange(uri, HttpMethod.PUT, entity, returnType);
  }

  public static void verifyForbiddenAccess(Runnable runnable) {
    try {
      runnable.run();
    } catch (HttpClientErrorException e) {
      Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      return;
    }
    Assertions.fail("Expected exception to be thrown");
  }

  private static Module createIdModule() {
    SimpleModule simpleModule = new SimpleModule();

    simpleModule.addSerializer(ImageTokenId.class, ImageTokenIdSerializer.INSTANCE);
    simpleModule.addSerializer(UserId.class, UserIdSerializer.INSTANCE);
    simpleModule.addSerializer(AnnouncementId.class, AnnouncementIdSerializer.INSTANCE);
    simpleModule.addSerializer(TeamPostId.class, TeamPostIdSerializer.INSTANCE);

    simpleModule.addDeserializer(ImageTokenId.class, ImageTokenIdDeserializer.INSTANCE);
    simpleModule.addDeserializer(UserId.class, UserIdDeserializer.INSTANCE);
    simpleModule.addDeserializer(AnnouncementId.class, AnnouncementIdDeserializer.INSTANCE);
    simpleModule.addDeserializer(TeamPostId.class, TeamPostIdDeserializer.INSTANCE);
    return simpleModule;
  }

  private static List<UserId> decodeBody(String jsonBody) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new SimpleModule().addDeserializer(UserId.class, UserIdDeserializer.INSTANCE));
    return objectMapper.readValue(jsonBody, objectMapper.getTypeFactory().constructCollectionType(List.class, UserId.class));
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(createIdModule());
    return objectMapper;
  }

  private RestUtil() {
    throw new IllegalStateException("Utility class");
  }
}
