package nu.borjessons.clubhouse.integration.tests;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.dto.TrainingTimeRecord;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.TeamUtil;
import nu.borjessons.clubhouse.integration.tests.util.TrainingTimeUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class TrainingTimeIntegrationTest {

  public static final String BIG_HALL = "Big Hall";
  public static final String SMALL_HALL = "Small Hall";

  private static String getTeamIdFromDatabase(ConfigurableApplicationContext configurableApplicationContext) {
    TeamRepository teamRepository = configurableApplicationContext.getBean(TeamRepository.class);
    return TeamUtil.getAllTeams(teamRepository).iterator().next().getTeamId();
  }

  @Test
  void createTrainingTimeTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String teamId = getTeamIdFromDatabase(configurableApplicationContext);
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      TrainingTimeRecord trainingTimeRecord1 = TrainingTimeUtil.createTrainingTime(token, teamId, TrainingTimeUtil.createTrainingTimeRequest(5, BIG_HALL));
      TrainingTimeUtil.checkCheckTrainingTimeRecord(trainingTimeRecord1, 5, BIG_HALL);

      TrainingTimeRecord trainingTimeRecord2 = TrainingTimeUtil.createTrainingTime(token, teamId, TrainingTimeUtil.createTrainingTimeRequest(6, SMALL_HALL));
      TrainingTimeUtil.checkCheckTrainingTimeRecord(trainingTimeRecord2, 6, SMALL_HALL);
    }
  }

  @Test
  void getScheduleTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String teamId = getTeamIdFromDatabase(configurableApplicationContext);
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TrainingTimeUtil.createTrainingTime(token, teamId, TrainingTimeUtil.createTrainingTimeRequest(5, BIG_HALL));
      TrainingTimeUtil.createTrainingTime(token, teamId, TrainingTimeUtil.createTrainingTimeRequest(6, SMALL_HALL));

      List<TrainingTimeRecord> trainingTimes = TrainingTimeUtil.getTrainingTimes(token, teamId);
      Assertions.assertEquals(2, trainingTimes.size());
      TrainingTimeUtil.checkCheckTrainingTimeRecord(trainingTimes.stream().filter(tt -> tt.dayOfWeek() == DayOfWeek.FRIDAY).findFirst().orElseThrow(), 5,
          BIG_HALL);
      TrainingTimeUtil.checkCheckTrainingTimeRecord(trainingTimes.stream().filter(tt -> tt.dayOfWeek() == DayOfWeek.SATURDAY).findFirst().orElseThrow(), 6,
          SMALL_HALL);

      Assertions.assertEquals(2, TeamUtil.getTeamById(EmbeddedDataLoader.CLUB_ID, teamId, token).getTrainingTimes().size());
    }
  }

  @Test
  void updateScheduleTest() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String teamId = getTeamIdFromDatabase(configurableApplicationContext);
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TrainingTimeRecord trainingTime = TrainingTimeUtil.createTrainingTime(token, teamId, TrainingTimeUtil.createTrainingTimeRequest(5, BIG_HALL));
      String trainingTimeId = trainingTime.trainingTimeId();
      TrainingTimeUtil.updateTrainingTime(token, teamId, trainingTimeId, TrainingTimeUtil.createTrainingTimeRequest(6, SMALL_HALL));
      TrainingTimeUtil.checkCheckTrainingTimeRecord(TrainingTimeUtil.getTrainingTimes(token, teamId).iterator().next(), 6, SMALL_HALL);
    }
  }

  @Test
  void deleteSchedule() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext configurableApplicationContext = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String teamId = getTeamIdFromDatabase(configurableApplicationContext);
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TrainingTimeRecord trainingTime = TrainingTimeUtil.createTrainingTime(token, teamId, TrainingTimeUtil.createTrainingTimeRequest(5, BIG_HALL));
      String trainingTimeId = trainingTime.trainingTimeId();

      String responseMessage = TrainingTimeUtil.deleteTrainingTime(token, teamId, trainingTimeId);
      Assertions.assertEquals(String.format("trainingTime with id %s was successfully deleted", trainingTimeId), responseMessage);

      List<TrainingTimeRecord> trainingTimes = TeamUtil.getTeamById(EmbeddedDataLoader.CLUB_ID, teamId, token).getTrainingTimes();
      Assertions.assertEquals(0, trainingTimes.size());
    }
  }
}
