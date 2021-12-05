package nu.borjessons.clubhouse.integration.tests;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.dto.ScheduleRecord;
import nu.borjessons.clubhouse.impl.dto.TrainingTimeRecord;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.ScheduleUtil;
import nu.borjessons.clubhouse.integration.tests.util.TeamUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ScheduleIntegrationTest {
  private static void checkScheduleRecord(ScheduleRecord scheduleRecord, String expectedTeamId) {
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
    Assertions.assertTrue(trainingTimes.stream().allMatch(trainingTime -> trainingTime.duration().equals(Duration.ofHours(2))));
  }

  private static String getTeamIdFromDatabase(ConfigurableApplicationContext configurableApplicationContext) {
    TeamRepository teamRepository = configurableApplicationContext.getBean(TeamRepository.class);
    return TeamUtil.getAllTeams(teamRepository).iterator().next().getTeamId();
  }

  @Test
  void createScheduleTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String teamId = getTeamIdFromDatabase(configurableApplicationContext);
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      ScheduleRecord scheduleRecord = ScheduleUtil.createSchedule(token, teamId);

      checkScheduleRecord(scheduleRecord, teamId);
    }
  }

  @Test
  void getScheduleTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String teamId = getTeamIdFromDatabase(configurableApplicationContext);
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      ScheduleUtil.createSchedule(token, teamId);

      ScheduleRecord scheduleRecord = ScheduleUtil.getSchedule(token, teamId);

      checkScheduleRecord(scheduleRecord, teamId);
    }
  }
}
