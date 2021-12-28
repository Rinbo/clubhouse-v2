package nu.borjessons.clubhouse.integration.tests;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.dto.ClubScheduleRecord;
import nu.borjessons.clubhouse.impl.dto.TeamScheduleRecord;
import nu.borjessons.clubhouse.impl.dto.TrainingTimeRecord;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.DbUtil;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.ScheduleUtil;
import nu.borjessons.clubhouse.integration.tests.util.TrainingTimeUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ScheduleIntegrationTest {
  public static final String BIG_HALL = "Big Hall";

  @Test
  void getClubScheduleTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      LocalDate today = LocalDate.now();
      String teamId = DbUtil.getTeamIdFromDatabase(configurableApplicationContext);
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TrainingTimeUtil.createTrainingTime(token, teamId, TrainingTimeUtil.createTrainingTimeRequest(today.getDayOfWeek().getValue(), "Big Hall"));

      validateClubSchedules(today, ScheduleUtil.getClubSchedule(EmbeddedDataLoader.CLUB_ID, token, today, today));
    }
  }

  @Test
  void getMyClubScheduleTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      LocalDate today = LocalDate.now();
      String teamId = DbUtil.getTeamIdFromDatabase(configurableApplicationContext);
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TrainingTimeUtil.createTrainingTime(token, teamId, TrainingTimeUtil.createTrainingTimeRequest(today.getDayOfWeek().getValue(), "Big Hall"));

      String daddyToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      validateClubSchedules(today, ScheduleUtil.getMyClubSchedule(EmbeddedDataLoader.CLUB_ID, daddyToken, today, today));
    }
  }

  @Test
  void getMyLeaderClubScheduleTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      LocalDate today = LocalDate.now();
      String teamId = DbUtil.getTeamIdFromDatabase(configurableApplicationContext);
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TrainingTimeUtil.createTrainingTime(token, teamId, TrainingTimeUtil.createTrainingTimeRequest(today.getDayOfWeek().getValue(), "Big Hall"));

      String daddyToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      validateClubSchedules(today, ScheduleUtil.getMyLeaderClubSchedule(EmbeddedDataLoader.CLUB_ID, daddyToken, today, today));
    }
  }

  @Test
  void getMyLeaderClubScheduleEmptyTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      LocalDate today = LocalDate.now();
      String teamId = DbUtil.getTeamIdFromDatabase(configurableApplicationContext);
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TrainingTimeUtil.createTrainingTime(token, teamId, TrainingTimeUtil.createTrainingTimeRequest(today.getDayOfWeek().getValue(), "Big Hall"));

      String mommyToken = UserUtil.loginUser(EmbeddedDataLoader.MOMMY_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      ClubScheduleRecord clubScheduleRecord = ScheduleUtil.getMyLeaderClubSchedule(EmbeddedDataLoader.CLUB_ID, mommyToken, today, today).iterator().next();
      Assertions.assertEquals(0, clubScheduleRecord.teamScheduleRecords().size());
    }
  }

  @Test
  void forbiddenTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      LocalDate today = LocalDate.now();
      String teamId = DbUtil.getTeamIdFromDatabase(configurableApplicationContext);
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TrainingTimeUtil.createTrainingTime(token, teamId, TrainingTimeUtil.createTrainingTimeRequest(today.getDayOfWeek().getValue(), "Big Hall"));

      String userToken = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      try {
        ScheduleUtil.getMyLeaderClubSchedule(EmbeddedDataLoader.CLUB_ID, userToken, today, today);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
        Assertions.assertTrue(e.getLocalizedMessage().contains("Access is denied"));
      }
    }
  }

  private void validateClubSchedules(LocalDate expectedLocalDate, List<ClubScheduleRecord> clubSchedule) {
    Assertions.assertEquals(1, clubSchedule.size());

    ClubScheduleRecord clubScheduleRecord = clubSchedule.iterator().next();
    Assertions.assertEquals("Fritiof Sports", clubScheduleRecord.clubName());
    Assertions.assertEquals(EmbeddedDataLoader.CLUB_ID, clubScheduleRecord.clubId());
    Assertions.assertEquals(expectedLocalDate, clubScheduleRecord.localDate());

    TeamScheduleRecord teamScheduleRecord = clubScheduleRecord.teamScheduleRecords().iterator().next();
    Assertions.assertNotNull(teamScheduleRecord);
    Assertions.assertEquals("Cool Team", teamScheduleRecord.teamName());
    Assertions.assertTrue(teamScheduleRecord.teamId().length() > 4);

    TrainingTimeRecord trainingTimeRecord = teamScheduleRecord.trainingTimeRecord();
    Assertions.assertEquals(BIG_HALL, trainingTimeRecord.location());
    Assertions.assertEquals(expectedLocalDate.getDayOfWeek(), trainingTimeRecord.dayOfWeek());
    Assertions.assertEquals(LocalTime.of(14, 0), trainingTimeRecord.startTime());
    Assertions.assertEquals(LocalTime.of(15, 0), trainingTimeRecord.endTime());
  }
}
