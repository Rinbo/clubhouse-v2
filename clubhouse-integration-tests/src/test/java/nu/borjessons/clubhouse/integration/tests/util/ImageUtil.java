package nu.borjessons.clubhouse.integration.tests.util;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import nu.borjessons.clubhouse.impl.ClubhouseApplication;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.util.FileUtils;

public class ImageUtil {
  public static final Path BASE_DIRECTORY = Paths.get(System.getProperty("java.io.tmpdir"), ClubhouseApplication.APP_NAME);

  public static ImageTokenId uploadClubLogo(String token, String clubId) throws IOException {
    URI uri = RestUtil.getUriBuilder("/clubs/{clubId}/upload-logo").buildAndExpand(clubId).toUri();
    return uploadAndCleanup(token, uri);
  }

  public static ImageTokenId uploadProfileImage(String token, UserId userId) throws IOException {
    URI uri = RestUtil.getUriBuilder("/users/{userId}/upload-profile-image").buildAndExpand(userId).toUri();
    return uploadAndCleanup(token, uri);
  }

  private static ImageTokenId uploadAndCleanup(String token, URI uri) throws IOException {
    Path resourcePath = Path.of("src/test/resources/files/tree.JPG");

    HttpHeaders httpHeaders = RestUtil.getHttpHeaders(token);
    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, Object> requestMap = new LinkedMultiValueMap<>();
    requestMap.add("file", new FileSystemResource(resourcePath.toFile()));

    HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(requestMap, httpHeaders);

    RestTemplate restTemplate = new RestTemplate();

    ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    ImageTokenId imageTokenId = RestUtil.deserializeJsonBody(response.getBody(), ImageTokenId.class);

    FileUtils.deleteDirectoryRecursively(BASE_DIRECTORY);
    return imageTokenId;
  }
}
