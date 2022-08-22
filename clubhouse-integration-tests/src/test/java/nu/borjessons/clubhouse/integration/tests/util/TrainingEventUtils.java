package nu.borjessons.clubhouse.integration.tests.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nu.borjessons.clubhouse.impl.dto.TrainingEventRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingEventRequestModel;

public class TrainingEventUtils {
  public static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2020, 1, 1, 12, 0);

  public static TrainingEventRecord create(String token, String clubId, String teamId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/training-events").buildAndExpand(clubId, teamId).toUriString();
    ResponseEntity<TrainingEventRecord> response = RestUtil.postRequest(uri, token, createTrainingEventRequestModel(), TrainingEventRecord.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  private static TrainingEventRequestModel createTrainingEventRequestModel() {
    return new TrainingEventRequestModel(LOCAL_DATE_TIME, Duration.ofHours(1), "My notes", List.of(),
        List.of());
  }

  private TrainingEventUtils() {
    throw new IllegalArgumentException("Utility class");
  }
}
