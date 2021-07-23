package nu.borjessons.clubhouse.integration.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import nu.borjessons.clubhouse.impl.dto.TeamDTO;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.TeamUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ClubTeamIntegrationTest {
  public static final String TEAM_NAME = "Team 1";

  @Test
  void adminCreatesTeamTest() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TeamRequestModel teamRequestModel = TeamUtil.createRequestModel(List.of(), TEAM_NAME);
      TeamDTO teamDTO = TeamUtil.createTeam(EmbeddedDataLoader.CLUB_ID, teamRequestModel, ownerToken);
      Assertions.assertNotNull(teamDTO);
      Assertions.assertNotNull(teamDTO.getTeamId());
      Assertions.assertEquals(TEAM_NAME, teamDTO.getName());
      Assertions.assertEquals(5, teamDTO.getMinAge());
      Assertions.assertEquals(20, teamDTO.getMaxAge());
      Assertions.assertTrue(teamDTO.getLeaders().isEmpty());
    }
  }

  @Test
  void userCannotCreateTeamTest() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String userToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TeamRequestModel teamRequestModel = TeamUtil.createRequestModel(List.of(), TEAM_NAME);
      try {
        TeamUtil.createTeam(EmbeddedDataLoader.CLUB_ID, teamRequestModel, userToken);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
    }
  }

  @Test
  void getMyTeamsTest() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String token = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      List<TeamDTO> teamDTOs = TeamUtil.getMyTeams(EmbeddedDataLoader.CLUB_ID, token);
      Assertions.assertEquals(1, teamDTOs.size());
    }
  }

  @Test
  void getTeams() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String userToken = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(1, TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, userToken).size());

      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TeamRequestModel teamRequestModel = TeamUtil.createRequestModel(List.of(), TEAM_NAME);
      TeamUtil.createTeam(EmbeddedDataLoader.CLUB_ID, teamRequestModel, ownerToken);
      Assertions.assertEquals(2, TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, userToken).size());
    }
  }

  @Test
  void getTeamById() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String token = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<TeamDTO> clubTeams = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, token);
      final String teamId = clubTeams.get(0).getTeamId();

      TeamDTO teamDTO = TeamUtil.getTeamById(EmbeddedDataLoader.CLUB_ID, teamId, token);
      Assertions.assertEquals(teamId, teamDTO.getTeamId());
    }
  }
}
