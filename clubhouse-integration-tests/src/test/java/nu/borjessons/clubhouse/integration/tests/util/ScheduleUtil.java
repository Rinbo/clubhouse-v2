package nu.borjessons.clubhouse.integration.tests.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import nu.borjessons.clubhouse.impl.dto.TrainingTimeRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingTimeRequest;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;

public class ScheduleUtil {
  public static ScheduleRecord createSchedule(String token, String teamId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/schedule").buildAndExpand(EmbeddedDataLoader.CLUB_ID, teamId).toUriString();
    ResponseEntity<ScheduleRecord> response = RestUtil.postRequest(uri, token, createScheduleRequest(), ScheduleRecord.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static ScheduleRecord getSchedule(String token, String teamId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/schedule").buildAndExpand(EmbeddedDataLoader.CLUB_ID, teamId).toUriString();
    ResponseEntity<ScheduleRecord> response = RestUtil.getRequest(uri, token, ScheduleRecord.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static void createAnotherSchedule(String token, String teamId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/teams/{teamId}/schedule").buildAndExpand(EmbeddedDataLoader.CLUB_ID, teamId).toUriString();
    ResponseEntity<ScheduleRecord> response = RestUtil.postRequest(uri, token, createAnotherSchedule(), ScheduleRecord.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  public static void checkScheduleRecord(ScheduleRecord scheduleRecord, String expectedTeamId) {
    List<TrainingTimeRecord> trainingTimes = scheduleRecord.trainingTimes();

    Assertions.assertNotNull(scheduleRecord);
    Assertions.assertEquals(expectedTeamId, scheduleRecord.teamId());
    Assertions.assertEquals("Cool Team", scheduleRecord.teamName());
    Assertions.assertEquals(LocalDate.of(2021, 1, 1), scheduleRecord.periodStart());
    Assertions.assertEquals(LocalDate.of(2021, 12, 31), scheduleRecord.periodEnd());
    Assertions.assertEquals(2, trainingTimes.size());
    Assertions.assertTrue(trainingTimes.stream().anyMatch(trainingTime -> trainingTime.dayOfWeek() == DayOfWeek.MONDAY));
    Assertions.assertTrue(trainingTimes.stream().anyMatch(trainingTime -> trainingTime.dayOfWeek() == DayOfWeek.WEDNESDAY));
    Assertions.assertTrue(trainingTimes.stream().anyMatch(trainingTime -> trainingTime.location().equals("Small Hall")));
    Assertions.assertTrue(trainingTimes.stream().anyMatch(trainingTime -> trainingTime.location().equals("Big Hall")));
    Assertions.assertTrue(trainingTimes.stream().allMatch(trainingTime -> trainingTime.startTime().equals(LocalTime.of(14, 0))));
    Assertions.assertTrue(trainingTimes.stream().allMatch(trainingTime -> trainingTime.endTime().equals(LocalTime.of(15, 0))));
  }

  public static void checkAnotherScheduleRecord(ScheduleRecord scheduleRecord, String expectedTeamId) {
    TrainingTimeRecord trainingTime = scheduleRecord.trainingTimes().iterator().next();

    Assertions.assertNotNull(scheduleRecord);
    Assertions.assertEquals(expectedTeamId, scheduleRecord.teamId());
    Assertions.assertEquals("Cool Team", scheduleRecord.teamName());
    Assertions.assertEquals(LocalDate.of(2020, 1, 1), scheduleRecord.periodStart());
    Assertions.assertEquals(LocalDate.of(2020, 12, 31), scheduleRecord.periodEnd());
    Assertions.assertEquals(1, scheduleRecord.trainingTimes().size());
    Assertions.assertEquals(DayOfWeek.FRIDAY, trainingTime.dayOfWeek());
    Assertions.assertEquals("Outdoors", trainingTime.location());
    Assertions.assertEquals(LocalTime.of(12, 0), trainingTime.startTime());
    Assertions.assertEquals(LocalTime.of(13, 0), trainingTime.endTime());
  }

  private static ScheduleRequest createAnotherSchedule() {
    ScheduleRequest scheduleRequest = new ScheduleRequest();
    scheduleRequest.setPeriodEnd(LocalDate.of(2020, 12, 31));
    scheduleRequest.setPeriodStart(LocalDate.of(2020, 1, 1));
    scheduleRequest.setTrainingTimes(createAnotherTrainingTimeRequests());
    return scheduleRequest;
  }

  private static List<TrainingTimeRequest> createAnotherTrainingTimeRequests() {
    TrainingTimeRequest trainingTimeRequest = new TrainingTimeRequest();
    trainingTimeRequest.setDayOfWeek(5);
    trainingTimeRequest.setLocation("Outdoors");
    trainingTimeRequest.setStartTime(LocalTime.of(12, 0));
    trainingTimeRequest.setEndTime(LocalTime.of(13, 0));
    return List.of(trainingTimeRequest);
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
    trainingTimeRequest1.setStartTime(LocalTime.of(14, 0));
    trainingTimeRequest1.setEndTime(LocalTime.of(15, 0));
    trainingTimeRequest1.setLocation("Big Hall");
    trainingTimeRequest1.setDayOfWeek(1);

    TrainingTimeRequest trainingTimeRequest2 = new TrainingTimeRequest();
    trainingTimeRequest2.setStartTime(LocalTime.of(14, 0));
    trainingTimeRequest2.setEndTime(LocalTime.of(15, 0));
    trainingTimeRequest2.setLocation("Small Hall");
    trainingTimeRequest2.setDayOfWeek(3);

    return List.of(trainingTimeRequest1, trainingTimeRequest2);
  }
}
