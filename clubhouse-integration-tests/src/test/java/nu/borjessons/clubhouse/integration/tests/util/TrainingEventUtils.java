package nu.borjessons.clubhouse.integration.tests.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.TrainingEventRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingEventRequestModel;

public class TrainingEventUtils {
  public static final LocalDateTime LOCAL_DATE_TIME_1 = LocalDateTime.of(2020, 1, 1, 12, 0);
  public static final LocalDateTime LOCAL_DATE_TIME_2 = LocalDateTime.of(2021, 1, 1, 12, 0);

  public static TrainingEventRecord create(String token, String clubId, String teamId, TrainingEventRequestModel trainingEventRequestModel)
      throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/training-events").buildAndExpand(clubId, teamId).toUriString();
    ResponseEntity<TrainingEventRecord> response = RestUtil.postSerializedRequest(uri, token, trainingEventRequestModel, TrainingEventRecord.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static TrainingEventRequestModel createTrainingEventRequestModel(LocalDateTime localDateTime, List<UserId> leaderIds, List<UserId> memberIds) {
    return new TrainingEventRequestModel(localDateTime, Duration.ofHours(1), "My notes", leaderIds, memberIds);
  }

  public static TrainingEventRequestModel createTrainingEventRequestModel(LocalDateTime localDateTime) {
    return createTrainingEventRequestModel(localDateTime, List.of(), List.of());
  }

  public static TrainingEventRecord getById(String token, String clubId, String teamId, long trainingEventId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/training-events/{trainingEventId}")
        .buildAndExpand(clubId, teamId, trainingEventId)
        .toUriString();

    ResponseEntity<TrainingEventRecord> response = RestUtil.getRequest(uri, token, TrainingEventRecord.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static List<TrainingEventRecord> getByTeamId(String token, String clubId, String teamId, int page, int size) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/training-events")
        .queryParam("page", page)
        .queryParam("size", size)
        .buildAndExpand(clubId, teamId).toUriString();

    ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return Arrays.stream(RestUtil.deserializeJsonBody(response.getBody(), TrainingEventRecord[].class)).toList();
  }

  public static TrainingEventRecord update(String token, String clubId, String teamId, long trainingEventId,
      TrainingEventRequestModel trainingEventRequestModel) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/training-events/{trainingEventId}")
        .buildAndExpand(clubId, teamId, trainingEventId)
        .toUriString();

    ResponseEntity<TrainingEventRecord> response = RestUtil.putSerializedRequest(uri, token, trainingEventRequestModel, TrainingEventRecord.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  private TrainingEventUtils() {
    throw new IllegalArgumentException("Utility class");
  }
}
