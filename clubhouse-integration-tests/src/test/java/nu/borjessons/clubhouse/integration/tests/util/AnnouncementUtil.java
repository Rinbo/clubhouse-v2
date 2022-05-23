package nu.borjessons.clubhouse.integration.tests.util;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;
import nu.borjessons.clubhouse.impl.dto.AnnouncementRecord;
import nu.borjessons.clubhouse.impl.dto.rest.AnnouncementModel;

public class AnnouncementUtil {
  public static AnnouncementRecord createAnnouncement(String token, String clubId, String title) throws JsonProcessingException {
    URI uri = RestUtil.getUriBuilder("/clubs/{clubId}/announcements")
        .buildAndExpand(clubId).toUri();

    HttpHeaders httpHeaders = RestUtil.getHttpHeaders(token);
    httpHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);

    MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<>();
    requestMap.add("title", title);
    requestMap.add("body", "body");

    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(requestMap, httpHeaders);

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    return RestUtil.deserializeJsonBody(response.getBody(), AnnouncementRecord.class);
  }

  public static void deleteAnnouncement(String token, String clubId, AnnouncementId announcementId) {
    URI uri = RestUtil.getUriBuilder("/clubs/{clubId}/announcements/{announcementId}").buildAndExpand(clubId, announcementId).toUri();
    ResponseEntity<Void> response = RestUtil.deleteRequest(uri.toString(), token, Void.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  public static AnnouncementRecord getAnnouncement(String token, String clubId, AnnouncementId announcementId) throws JsonProcessingException {
    URI uri = RestUtil.getUriBuilder("/clubs/{clubId}/announcements/{announcementId}").buildAndExpand(clubId, announcementId).toUri();
    ResponseEntity<String> response = RestUtil.getRequest(uri.toString(), token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), AnnouncementRecord.class);
  }

  public static int getAnnouncementSize(String token, String clubId) {
    URI uri = RestUtil.getUriBuilder("/clubs/{clubId}/announcements/size").buildAndExpand(clubId).toUri();
    ResponseEntity<Integer> response = RestUtil.getRequest(uri.toString(), token, Integer.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Integer count = response.getBody();
    Assertions.assertNotNull(count);
    return count;
  }

  public static List<AnnouncementRecord> getAnnouncements(String token, String clubId) throws JsonProcessingException {
    URI uri = RestUtil.getUriBuilder("/clubs/{clubId}/announcements").buildAndExpand(clubId).toUri();
    ResponseEntity<String> response = RestUtil.getRequest(uri.toString(), token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return Arrays.stream(RestUtil.deserializeJsonBody(response.getBody(), AnnouncementRecord[].class)).toList();
  }

  public static List<AnnouncementRecord> getPageableAnnouncements(String token, String clubId, int page, int size) throws JsonProcessingException {
    URI uri = RestUtil.getUriBuilder("/clubs/{clubId}/announcements")
        .queryParam("page", page)
        .queryParam("size", size)
        .buildAndExpand(clubId).toUri();

    ResponseEntity<String> response = RestUtil.getRequest(uri.toString(), token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return Arrays.stream(RestUtil.deserializeJsonBody(response.getBody(), AnnouncementRecord[].class)).toList();
  }

  public static List<AnnouncementRecord> getPrincipalAnnouncements(String token, int page, int size) throws JsonProcessingException {
    URI uri = RestUtil.getUriBuilder("/principal/announcements")
        .queryParam("page", page)
        .queryParam("size", size)
        .buildAndExpand().toUri();

    ResponseEntity<String> response = RestUtil.getRequest(uri.toString(), token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return Arrays.stream(RestUtil.deserializeJsonBody(response.getBody(), AnnouncementRecord[].class)).toList();
  }

  public static AnnouncementRecord updateAnnouncement(String token, String clubId, AnnouncementId announcementId)
      throws JsonProcessingException {
    URI uri = RestUtil.getUriBuilder("/clubs/{clubId}/announcements/{announcementId}").buildAndExpand(clubId, announcementId).toUri();
    ResponseEntity<String> response = RestUtil.putRequest(uri.toString(), token, new AnnouncementModel("updated title", "updated body"), String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), AnnouncementRecord.class);
  }
}
