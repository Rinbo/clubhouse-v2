package nu.borjessons.clubhouse.integration.tests.util;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;
import nu.borjessons.clubhouse.impl.dto.AnnouncementRecord;
import nu.borjessons.clubhouse.impl.dto.rest.AnnouncementModel;

public class AnnouncementUtil {
  public static AnnouncementRecord createAnnouncement(String token, String clubId, String title) throws JsonProcessingException {
    URI uri = RestUtil.getUriBuilder("/clubs/{clubId}/announcement").buildAndExpand(clubId).toUri();
    ResponseEntity<String> response = RestUtil.postRequest(uri.toString(), token, new AnnouncementModel(title, "body"), String.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);

    return RestUtil.deserializeJsonBody(response.getBody(), AnnouncementRecord.class);
  }

  public static void deleteAnnouncement(String token, String clubId, AnnouncementId announcementId) {
    URI uri = RestUtil.getUriBuilder("/clubs/{clubId}/announcement/{announcementId}").buildAndExpand(clubId, announcementId).toUri();
    ResponseEntity<Void> response = RestUtil.deleteRequest(uri.toString(), token, Void.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  public static List<AnnouncementRecord> getAllAnnouncements(String token, String clubId) throws JsonProcessingException {
    URI uri = RestUtil.getUriBuilder("/clubs/{clubId}/announcement").buildAndExpand(clubId).toUri();
    ResponseEntity<String> response = RestUtil.getRequest(uri.toString(), token, String.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    return Arrays.stream(RestUtil.deserializeJsonBody(response.getBody(), AnnouncementRecord[].class)).toList();
  }

  public static AnnouncementRecord getAnnouncement(String token, String clubId, AnnouncementId announcementId) throws JsonProcessingException {
    URI uri = RestUtil.getUriBuilder("/clubs/{clubId}/announcement/{announcementId}").buildAndExpand(clubId, announcementId).toUri();
    ResponseEntity<String> response = RestUtil.getRequest(uri.toString(), token, String.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    return RestUtil.deserializeJsonBody(response.getBody(), AnnouncementRecord.class);
  }

  public static AnnouncementRecord updateAnnouncement(String token, String clubId, AnnouncementId announcementId) throws JsonProcessingException {
    URI uri = RestUtil.getUriBuilder("/clubs/{clubId}/announcement/{announcementId}").buildAndExpand(clubId, announcementId).toUri();
    ResponseEntity<String> response = RestUtil.putRequest(uri.toString(), token, new AnnouncementModel("updated title", "updated body"), String.class);
    Assertions.assertEquals(response.getStatusCode(), HttpStatus.OK);
    return RestUtil.deserializeJsonBody(response.getBody(), AnnouncementRecord.class);
  }
}
