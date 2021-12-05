package nu.borjessons.clubhouse.integration.tests.util;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nu.borjessons.clubhouse.impl.dto.ScheduleRecord;
import nu.borjessons.clubhouse.impl.dto.rest.ScheduleRequest;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingTimeRequest;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;

public class ScheduleUtil {
  public static ScheduleRecord createSchedule(String token, String teamId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/schedule").buildAndExpand(EmbeddedDataLoader.CLUB_ID, teamId).toUriString();
    ResponseEntity<ScheduleRecord> response = RestUtil.postRequest(uri, token, createScheduleRequest(), ScheduleRecord.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  private static ScheduleRequest createScheduleRequest() {
    ScheduleRequest scheduleRequest = new ScheduleRequest();
    scheduleRequest.setPeriodEnd(LocalDate.of(2021, 12, 31));
    scheduleRequest.setPeriodStart(LocalDate.of(2021, 1, 1));
    scheduleRequest.setTrainingTimes(createTrainingTimeRequests());
    return scheduleRequest;
  }

  private static List<TrainingTimeRequest> createTrainingTimeRequests() {
    TrainingTimeRequest trainingTimeRequest1 = new TrainingTimeRequest();
    trainingTimeRequest1.setDuration(Duration.ofHours(2));
    trainingTimeRequest1.setLocation("Big Hall");
    trainingTimeRequest1.setDayOfWeek(1);

    TrainingTimeRequest trainingTimeRequest2 = new TrainingTimeRequest();
    trainingTimeRequest2.setDuration(Duration.ofHours(2));
    trainingTimeRequest2.setLocation("Small Hall");
    trainingTimeRequest2.setDayOfWeek(3);

    return List.of(trainingTimeRequest1, trainingTimeRequest2);
  }
}
