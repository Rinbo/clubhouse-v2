package nu.borjessons.clubhouse.integration.tests;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.dto.ScheduleRecord;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.ScheduleUtil;
import nu.borjessons.clubhouse.integration.tests.util.TeamUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ScheduleIntegrationTest {
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

      ScheduleUtil.checkScheduleRecord(scheduleRecord, teamId);
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

      ScheduleUtil.checkScheduleRecord(scheduleRecord, teamId);
    }
  }

  @Test
  void updateScheduleTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String teamId = getTeamIdFromDatabase(configurableApplicationContext);
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      ScheduleUtil.createSchedule(token, teamId);
      ScheduleRecord scheduleRecord = ScheduleUtil.getSchedule(token, teamId);
      ScheduleUtil.checkScheduleRecord(scheduleRecord, teamId);

      ScheduleUtil.createAnotherSchedule(token, teamId);
      ScheduleRecord anotherSchedule = ScheduleUtil.getSchedule(token, teamId);
      ScheduleUtil.checkAnotherScheduleRecord(anotherSchedule, teamId);
    }
  }
}
