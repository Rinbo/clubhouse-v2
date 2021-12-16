package nu.borjessons.clubhouse.integration.tests.util;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.dto.TrainingTimeRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingTimeRequest;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;

public class TrainingTimeUtil {
  public static TrainingTimeRecord createTrainingTime(String token, String teamId, TrainingTimeRequest trainingTimeRequest) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/training-time").buildAndExpand(EmbeddedDataLoader.CLUB_ID, teamId).toUriString();
    ResponseEntity<TrainingTimeRecord> response = RestUtil.postRequest(uri, token, trainingTimeRequest, TrainingTimeRecord.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static List<TrainingTimeRecord> getTrainingTimes(String token, String teamId) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/training-time").buildAndExpand(EmbeddedDataLoader.CLUB_ID, teamId).toUriString();
    ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return Arrays.stream(RestUtil.deserializeJsonBody(response.getBody(), TrainingTimeRecord[].class)).toList();
  }

  public static TrainingTimeRecord updateTrainingTime(String token, String teamId, String trainingTimeId, TrainingTimeRequest trainingTimeRequest) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/training-time/{trainingTimeId}")
        .buildAndExpand(EmbeddedDataLoader.CLUB_ID, teamId, trainingTimeId).toUriString();
    ResponseEntity<TrainingTimeRecord> response = RestUtil.putRequest(uri, token, trainingTimeRequest, TrainingTimeRecord.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static void checkCheckTrainingTimeRecord(TrainingTimeRecord trainingTimeRecord, int expectedDayOfWeek, String expectedLocation) {
    Assertions.assertNotNull(trainingTimeRecord);
    Assertions.assertEquals(expectedDayOfWeek, trainingTimeRecord.dayOfWeek().getValue());
    Assertions.assertEquals(expectedLocation, trainingTimeRecord.location());
    Assertions.assertEquals(LocalTime.of(14, 0), trainingTimeRecord.startTime());
    Assertions.assertEquals(LocalTime.of(15, 0), trainingTimeRecord.endTime());
  }

  public static TrainingTimeRequest createTrainingTimeRequest(int dayOfWeek, String location) {
    TrainingTimeRequest trainingTimeRequest = new TrainingTimeRequest();
    trainingTimeRequest.setStartTime(LocalTime.of(14, 0));
    trainingTimeRequest.setEndTime(LocalTime.of(15, 0));
    trainingTimeRequest.setLocation(location);
    trainingTimeRequest.setDayOfWeek(dayOfWeek);

    return trainingTimeRequest;
  }

  public static String deleteTrainingTime(String token, String teamId, String trainingTimeId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/training-time/{trainingTimeId}")
        .buildAndExpand(EmbeddedDataLoader.CLUB_ID, teamId, trainingTimeId).toUriString();
    ResponseEntity<String> response = RestUtil.deleteRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }
}
