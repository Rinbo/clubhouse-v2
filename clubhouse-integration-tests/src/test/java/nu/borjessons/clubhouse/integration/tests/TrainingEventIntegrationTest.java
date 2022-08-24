package nu.borjessons.clubhouse.integration.tests;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.dto.TrainingEventRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TrainingEventRequestModel;
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

      TrainingEventRequestModel trainingEventRequestModel = TrainingEventUtils.createTrainingEventRequestModel(TrainingEventUtils.LOCAL_DATE_TIME_1);
      TrainingEventRecord trainingEventRecord = TrainingEventUtils.create(token, EmbeddedDataLoader.CLUB_ID, teamId, trainingEventRequestModel);
      Assertions.assertEquals("My notes", trainingEventRecord.notes());
      Assertions.assertEquals(TrainingEventUtils.LOCAL_DATE_TIME_1, trainingEventRecord.dateTime());
      Assertions.assertEquals(Duration.ofHours(1), trainingEventRecord.duration());
      Assertions.assertEquals(List.of(), trainingEventRecord.presentLeaders());
      Assertions.assertEquals(List.of(), trainingEventRecord.presentMembers());
    }
  }

  @Test
  void getPageableTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      String teamId = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, token).get(0).getTeamId();
      TrainingEventUtils.create(token, EmbeddedDataLoader.CLUB_ID, teamId,
          TrainingEventUtils.createTrainingEventRequestModel(TrainingEventUtils.LOCAL_DATE_TIME_2));
      TrainingEventUtils.create(token, EmbeddedDataLoader.CLUB_ID, teamId,
          TrainingEventUtils.createTrainingEventRequestModel(TrainingEventUtils.LOCAL_DATE_TIME_1));

      Assertions.assertEquals(1, TrainingEventUtils.getByTeamId(token, EmbeddedDataLoader.CLUB_ID, teamId, 0, 1).size());
      Assertions.assertEquals(2, TrainingEventUtils.getByTeamId(token, EmbeddedDataLoader.CLUB_ID, teamId, 0, 2).size());
      Assertions.assertEquals(0, TrainingEventUtils.getByTeamId(token, EmbeddedDataLoader.CLUB_ID, teamId, 1, 2).size());
    }
  }

  @Test
  void updateTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      String teamId = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, token).get(0).getTeamId();
      long trainingEventId = TrainingEventUtils.create(token, EmbeddedDataLoader.CLUB_ID, teamId,
          TrainingEventUtils.createTrainingEventRequestModel(TrainingEventUtils.LOCAL_DATE_TIME_1)).id();

      UserId leaderId = UserUtil.getUserIdByEmail("pops@ex.com", configurableApplicationContext);
      UserId memberId = UserUtil.getUserIdByEmail("user@ex.com", configurableApplicationContext);

      TrainingEventRequestModel trainingEventRequestModel = TrainingEventUtils.createTrainingEventRequestModel(TrainingEventUtils.LOCAL_DATE_TIME_2,
          List.of(leaderId), List.of(memberId));
      TrainingEventRecord trainingEventRecord = TrainingEventUtils.update(token, EmbeddedDataLoader.CLUB_ID, teamId, trainingEventId,
          trainingEventRequestModel);
      List<BaseUserRecord> presentLeaders = trainingEventRecord.presentLeaders();
      List<BaseUserRecord> presentMembers = trainingEventRecord.presentMembers();

      Assertions.assertEquals(trainingEventId, trainingEventRecord.id());
      Assertions.assertEquals(1, presentLeaders.size());
      Assertions.assertEquals(1, presentMembers.size());
      Assertions.assertEquals(leaderId.toString(), presentLeaders.get(0).userId());
      Assertions.assertEquals(memberId.toString(), presentMembers.get(0).userId());
      Assertions.assertEquals(TrainingEventUtils.LOCAL_DATE_TIME_2, trainingEventRecord.dateTime());
      Assertions.assertEquals(Duration.ofHours(1), trainingEventRecord.duration());
      Assertions.assertEquals("My notes", trainingEventRecord.notes());
    }
  }
}
