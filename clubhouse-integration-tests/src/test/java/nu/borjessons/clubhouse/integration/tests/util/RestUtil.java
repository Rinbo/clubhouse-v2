package nu.borjessons.clubhouse.integration.tests.util;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;

import org.junit.jupiter.api.Assertions;
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

public class RestUtil {
  public static final String BASE_URL = "http://localhost:8081";

  public static UriComponentsBuilder getUriBuilder(String path) {
    return UriComponentsBuilder.fromHttpUrl(BASE_URL).path(path);
  }

  public static <T> T deserializeJsonBody(String body, Class<T> clazz) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new ParameterNamesModule());
    mapper.setVisibility(FIELD, ANY);
    return mapper.readValue(body, clazz);
  }

  public static <U> ResponseEntity<U> deleteRequest(String uri, String token, Class<U> returnType) {
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = getVoidHttpEntity(token);
    return restTemplate.exchange(uri, HttpMethod.DELETE, entity, returnType);
  }

  public static <T, U> ResponseEntity<U> postRequest(String uri, String token, T requestObject, Class<U> returnType) {
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<T> entity = getHttpEntity(token, requestObject);
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

  public static <T> HttpEntity<T> getHttpEntity(String token, T requestObject) {
    HttpHeaders headers = getHttpHeaders(token);
    return new HttpEntity<T>(requestObject, headers);
  }

  public static HttpEntity<Void> getVoidHttpEntity(String token) {
    HttpHeaders headers = getHttpHeaders(token);
    return new HttpEntity<>(headers);
  }

  private RestUtil() {
    throw new IllegalStateException("Utility class");
  }
}
