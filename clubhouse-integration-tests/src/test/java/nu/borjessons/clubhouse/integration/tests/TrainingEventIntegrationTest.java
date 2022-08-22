package nu.borjessons.clubhouse.integration.tests;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.dto.TrainingEventRecord;
import nu.borjessons.clubhouse.impl.util.dev.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.TeamUtil;
import nu.borjessons.clubhouse.integration.tests.util.TrainingEventUtils;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class TrainingEventIntegrationTest {

  @Test
  void createTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      String teamId = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, token).get(0).getTeamId();

      TrainingEventRecord trainingEventRecord = TrainingEventUtils.create(token, EmbeddedDataLoader.CLUB_ID, teamId);
      Assertions.assertEquals("My notes", trainingEventRecord.notes());
      Assertions.assertEquals(TrainingEventUtils.LOCAL_DATE_TIME, trainingEventRecord.dateTime());
      Assertions.assertEquals(Duration.ofHours(1), trainingEventRecord.duration());
      Assertions.assertEquals(List.of(), trainingEventRecord.presentLeaders());
      Assertions.assertEquals(List.of(), trainingEventRecord.presentMembers());
    }
  }
}
