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
  private static void verifyDefaultTrainingRecord(TrainingEventRecord trainingEventRecord) {
    Assertions.assertEquals("My notes", trainingEventRecord.notes());
    Assertions.assertEquals(TrainingEventUtils.LOCAL_DATE_TIME_1, trainingEventRecord.dateTime());
    Assertions.assertEquals(Duration.ofHours(1), trainingEventRecord.duration());
    Assertions.assertEquals(List.of(), trainingEventRecord.presentLeaders());
    Assertions.assertEquals(List.of(), trainingEventRecord.presentMembers());
  }

  @Test
  void createTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      String teamId = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, token).get(0).getTeamId();

      TrainingEventRecord trainingEventRecord1 = TrainingEventUtils.create(token, EmbeddedDataLoader.CLUB_ID, teamId,
          TrainingEventUtils.createTrainingEventRequestModel(TrainingEventUtils.LOCAL_DATE_TIME_1));
      verifyDefaultTrainingRecord(trainingEventRecord1);

      UserId nonLeaderId = UserUtil.getUserIdByEmail("user@ex.com", configurableApplicationContext);
      TrainingEventRecord trainingEventRecord2 = TrainingEventUtils.create(token, EmbeddedDataLoader.CLUB_ID, teamId,
          TrainingEventUtils.createTrainingEventRequestModel(TrainingEventUtils.LOCAL_DATE_TIME_1, List.of(nonLeaderId), List.of()));
      verifyDefaultTrainingRecord(trainingEventRecord2);
    }
  }

  @Test
  void deleteTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      String teamId = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, token).get(0).getTeamId();
      long trainingEventId1 = TrainingEventUtils.create(token, EmbeddedDataLoader.CLUB_ID, teamId,
          TrainingEventUtils.createTrainingEventRequestModel(TrainingEventUtils.LOCAL_DATE_TIME_1)).id();

      TrainingEventUtils.delete(token, EmbeddedDataLoader.CLUB_ID, teamId, trainingEventId1);

      UserId memberId1 = UserUtil.getUserIdByEmail("user@ex.com", configurableApplicationContext);
      UserId leaderId1 = UserUtil.getUserIdByEmail("pops@ex.com", configurableApplicationContext);

      TrainingEventRequestModel trainingEventRequestModel = TrainingEventUtils.createTrainingEventRequestModel(TrainingEventUtils.LOCAL_DATE_TIME_2,
          List.of(leaderId1), List.of(memberId1));
      long trainingEventId2 = TrainingEventUtils.create(token, EmbeddedDataLoader.CLUB_ID, teamId, trainingEventRequestModel).id();

      UserUtil.removeClubUser(token, EmbeddedDataLoader.CLUB_ID, memberId1);
      Assertions.assertTrue(TrainingEventUtils.getById(token, EmbeddedDataLoader.CLUB_ID, teamId, trainingEventId2).presentMembers().isEmpty());
      Assertions.assertFalse(TrainingEventUtils.getById(token, EmbeddedDataLoader.CLUB_ID, teamId, trainingEventId2).presentLeaders().isEmpty());

      UserUtil.removeClubUser(token, EmbeddedDataLoader.CLUB_ID, leaderId1);
      Assertions.assertTrue(TrainingEventUtils.getById(token, EmbeddedDataLoader.CLUB_ID, teamId, trainingEventId2).presentMembers().isEmpty());
      Assertions.assertTrue(TrainingEventUtils.getById(token, EmbeddedDataLoader.CLUB_ID, teamId, trainingEventId2).presentLeaders().isEmpty());

      UserId leaderId2 = UserUtil.getUserIdByEmail("mommy@ex.com", configurableApplicationContext);
      TrainingEventUtils.create(token, EmbeddedDataLoader.CLUB_ID, teamId,
          TrainingEventUtils.createTrainingEventRequestModel(TrainingEventUtils.LOCAL_DATE_TIME_2, List.of(leaderId2), List.of()));
      TeamUtil.deleteTeam(token, EmbeddedDataLoader.CLUB_ID, teamId);
      Assertions.assertEquals(0, TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, token).size());
      Assertions.assertEquals(leaderId2, UserUtil.getClubUser(token, EmbeddedDataLoader.CLUB_ID, leaderId2).getUserId());
    }
  }

  @Test
  void getByIdTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      String teamId = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, token).get(0).getTeamId();
      long trainingEventId = TrainingEventUtils.create(token, EmbeddedDataLoader.CLUB_ID, teamId,
          TrainingEventUtils.createTrainingEventRequestModel(TrainingEventUtils.LOCAL_DATE_TIME_1)).id();

      String userToken = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TrainingEventRecord trainingEventRecord = TrainingEventUtils.getById(userToken, EmbeddedDataLoader.CLUB_ID, teamId, trainingEventId);
      verifyDefaultTrainingRecord(trainingEventRecord);
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
