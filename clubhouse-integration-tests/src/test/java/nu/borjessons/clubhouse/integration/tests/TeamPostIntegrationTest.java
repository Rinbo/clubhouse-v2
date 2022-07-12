package nu.borjessons.clubhouse.integration.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.data.key.TeamPostId;
import nu.borjessons.clubhouse.impl.dto.TeamPostCommentRecord;
import nu.borjessons.clubhouse.impl.dto.TeamPostRecord;
import nu.borjessons.clubhouse.impl.util.dev.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.TeamPostUtil;
import nu.borjessons.clubhouse.integration.tests.util.TeamUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class TeamPostIntegrationTest {
  private static TeamPostRecord createTeamPostRecord(String token) throws JsonProcessingException {
    String teamId = TeamUtil.getMyTeams(EmbeddedDataLoader.CLUB_ID, token).iterator().next().getTeamId();
    TeamPostId teamPostId = TeamPostUtil.create(token, EmbeddedDataLoader.CLUB_ID, teamId).teamPostId();
    return TeamPostUtil.createComment(token, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId);
  }

  private static void verifyNoAccess(String userToken, String teamId, TeamPostId teamPostId) {
    try {
      TeamPostUtil.delete(userToken, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId);
    } catch (HttpClientErrorException e) {
      Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
    }
  }

  private static void verifyTeamPost(TeamPostRecord teamPostRecord, String expectedTeamId, String expectedTitle, String expectedBody) {
    Assertions.assertEquals(expectedTeamId, teamPostRecord.teamId());
    Assertions.assertEquals(expectedTitle, teamPostRecord.title());
    Assertions.assertEquals(expectedBody, teamPostRecord.body());
    Assertions.assertNotNull(teamPostRecord.teamPostId());
    Assertions.assertNotNull(teamPostRecord.author());
    Assertions.assertNotNull(teamPostRecord.createdAt());
  }

  @Test
  void createAndDeleteTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      String teamId = TeamUtil.getMyTeams(EmbeddedDataLoader.CLUB_ID, token).iterator().next().getTeamId();

      TeamPostRecord teamPostRecord = TeamPostUtil.create(token, EmbeddedDataLoader.CLUB_ID, teamId);
      verifyTeamPost(teamPostRecord, teamId, TeamPostUtil.TITLE, TeamPostUtil.BODY);

      String userToken = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TeamPostId teamPostId = teamPostRecord.teamPostId();

      verifyNoAccess(userToken, teamId, teamPostId);

      verifyTeamPost(TeamPostUtil.get(userToken, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId), teamId, TeamPostUtil.TITLE,
          TeamPostUtil.BODY);
      Assertions.assertEquals(1, TeamPostUtil.getAll(token, EmbeddedDataLoader.CLUB_ID, teamId).size());

      TeamPostUtil.delete(token, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId);
      Assertions.assertTrue(TeamPostUtil.getAll(token, EmbeddedDataLoader.CLUB_ID, teamId).isEmpty());
    }
  }

  @Test
  void createComment() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      TeamPostRecord teamPostRecord = createTeamPostRecord(UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD));
      TeamPostCommentRecord teamPostCommentRecord = teamPostRecord.comments().iterator().next();
      Assertions.assertEquals("a comment", teamPostCommentRecord.comment());
      Assertions.assertNotEquals(0, teamPostCommentRecord.id());
      Assertions.assertNotNull(teamPostCommentRecord.createdAt());
      Assertions.assertEquals(EmbeddedDataLoader.USER_ID.toString(), teamPostCommentRecord.author().userId());
    }
  }

  @Test
  void getComments() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TeamPostRecord teamPostRecord = createTeamPostRecord(token);

      String teamId = teamPostRecord.teamId();
      TeamPostId teamPostId = teamPostRecord.teamPostId();

      TeamPostUtil.createComment(token, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId);
      TeamPostUtil.createComment(token, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId);
      TeamPostUtil.createComment(token, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId);

      Assertions.assertEquals(4, TeamPostUtil.getComments(token, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId, 0, 10).size());
      Assertions.assertEquals(2, TeamPostUtil.getComments(token, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId, 0, 2).size());
      Assertions.assertEquals(2, TeamPostUtil.getComments(token, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId, 1, 2).size());
    }
  }

  @Test
  void updateAndDeleteComment() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TeamPostRecord teamPostRecord = createTeamPostRecord(token);
      String teamId = teamPostRecord.teamId();
      TeamPostId teamPostId = teamPostRecord.teamPostId();
      long teamPostCommentId = teamPostRecord.comments().iterator().next().id();

      TeamPostCommentRecord teamPostCommentRecord = TeamPostUtil.updateComment(token, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId, teamPostCommentId)
          .comments()
          .iterator()
          .next();

      Assertions.assertEquals("updated Comment", teamPostCommentRecord.comment());
      TeamPostUtil.deleteComment(token, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId, teamPostCommentId);
      Assertions.assertEquals(0, TeamPostUtil.getComments(token, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId, 0, 10).size());
    }
  }

  @Test
  void updateDeleteTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      String teamId = TeamUtil.getMyTeams(EmbeddedDataLoader.CLUB_ID, token).iterator().next().getTeamId();
      TeamPostId teamPostId = TeamPostUtil.create(token, EmbeddedDataLoader.CLUB_ID, teamId).teamPostId();

      verifyTeamPost(TeamPostUtil.getAll(token, EmbeddedDataLoader.CLUB_ID, teamId).iterator().next(), teamId, TeamPostUtil.TITLE, TeamPostUtil.BODY);
      verifyTeamPost(TeamPostUtil.update(token, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId), teamId, "Updated title", "Updated body");

      TeamPostUtil.delete(token, EmbeddedDataLoader.CLUB_ID, teamId, teamPostId);
      Assertions.assertTrue(TeamPostUtil.getAll(token, EmbeddedDataLoader.CLUB_ID, teamId).isEmpty());
    }
  }
}
