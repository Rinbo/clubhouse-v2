package nu.borjessons.clubhouse.integration.tests.util;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.dto.ClubScheduleRecord;

public class ScheduleUtil {

  public static List<ClubScheduleRecord> getClubSchedule(String clubId, String token, LocalDate startDate, LocalDate endDate) throws JsonProcessingException {
    return getClubSchedule("/clubs/{clubId}/schedule", clubId, token, startDate, endDate);
  }

  public static List<ClubScheduleRecord> getMyClubSchedule(String clubId, String token, LocalDate startDate, LocalDate endDate)
      throws JsonProcessingException {
    return getClubSchedule("/clubs/{clubId}/my-schedule", clubId, token, startDate, endDate);
  }

  public static List<ClubScheduleRecord> getMyLeaderClubSchedule(String clubId, String token, LocalDate startDate, LocalDate endDate)
      throws JsonProcessingException {
    return getClubSchedule("/clubs/{clubId}/leader/my-schedule", clubId, token, startDate, endDate);
  }

  private static List<ClubScheduleRecord> getClubSchedule(String baseUri, String clubId, String token, LocalDate startDate, LocalDate endDate)
      throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder(baseUri)
        .queryParam("startDate", startDate)
        .queryParam("endDate", endDate)
        .buildAndExpand(clubId).toUriString();

    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);
    ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return Arrays.stream(RestUtil.deserializeJsonBody(response.getBody(), ClubScheduleRecord[].class)).toList();
  }
}
