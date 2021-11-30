package nu.borjessons.clubhouse.integration.tests;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.dto.ScheduleRecord;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.ScheduleUtil;
import nu.borjessons.clubhouse.integration.tests.util.TeamUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ScheduleIntegrationTest {

  @Test
  void createScheduleTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      TeamRepository teamRepository = configurableApplicationContext.getBean(TeamRepository.class);
      Team team = TeamUtil.getAllTeams(teamRepository).iterator().next();

      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      ScheduleRecord scheduleRecord = ScheduleUtil.createSchedule(token, team.getTeamId());
      Assertions.assertNotNull(scheduleRecord);
    }
  }
}
