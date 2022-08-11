package nu.borjessons.clubhouse.integration.tests;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.TeamDto;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;
import nu.borjessons.clubhouse.impl.util.dev.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.TeamUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ClubTeamIntegrationTest {
  public static final String TEAM_NAME = "Team 1";

  @Test
  void adminCreatesTeamTest() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TeamRequestModel teamRequestModel = TeamUtil.createRequestModel(List.of(), List.of(), TEAM_NAME);
      TeamDto teamDTO = TeamUtil.createTeam(EmbeddedDataLoader.CLUB_ID, teamRequestModel, ownerToken);
      Assertions.assertNotNull(teamDTO);
      Assertions.assertNotNull(teamDTO.getTeamId());
      Assertions.assertEquals(TEAM_NAME, teamDTO.getName());
      Assertions.assertEquals("Generic team", teamDTO.getDescription());
      Assertions.assertTrue(teamDTO.getLeaders().isEmpty());
      Assertions.assertTrue(teamDTO.getMembers().isEmpty());
    }
  }

  @Test
  void getMyTeamsTest() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      List<TeamDto> teamDtos = TeamUtil.getMyTeams(EmbeddedDataLoader.CLUB_ID, token);
      Assertions.assertEquals(1, teamDtos.size());
    }
  }

  @Test
  void getTeamById() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      List<TeamDto> clubTeams = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, token);
      String teamId = clubTeams.get(0).getTeamId();

      TeamDto teamDTO = TeamUtil.getTeamById(EmbeddedDataLoader.CLUB_ID, teamId, token);
      Assertions.assertEquals(teamId, teamDTO.getTeamId());
    }
  }

  @Test
  void getTeams() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String userToken = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(1, TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, userToken).size());

      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TeamRequestModel teamRequestModel = TeamUtil.createRequestModel(List.of(), List.of(), TEAM_NAME);
      TeamUtil.createTeam(EmbeddedDataLoader.CLUB_ID, teamRequestModel, ownerToken);
      Assertions.assertEquals(2, TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, userToken).size());
    }
  }

  @Test
  void joinAndLeaveTeam() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TeamRequestModel teamRequestModel = TeamUtil.createRequestModel(List.of(), List.of(), TEAM_NAME);
      TeamDto newTeamDto = TeamUtil.createTeam(EmbeddedDataLoader.CLUB_ID, teamRequestModel, ownerToken);

      String token = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      String teamId = newTeamDto.getTeamId();
      TeamDto teamDTO = TeamUtil.joinTeam(EmbeddedDataLoader.CLUB_ID, teamId, token);
      Assertions.assertEquals(teamId, teamDTO.getTeamId());
      Assertions.assertEquals(2, TeamUtil.getMyTeams(EmbeddedDataLoader.CLUB_ID, token).size());

      TeamUtil.leaveTeam(EmbeddedDataLoader.CLUB_ID, teamId, token);
      Assertions.assertEquals(1, TeamUtil.getMyTeams(EmbeddedDataLoader.CLUB_ID, token).size());
    }
  }

  @Test
  void removeLeaderFromTeamTest() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserId leaderId = UserUtil.getUserIdByEmail(EmbeddedDataLoader.POPS_EMAIL, context);
      List<TeamDto> teamDtos = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, ownerToken);
      String teamId = teamDtos.get(0).getTeamId();

      Assertions.assertEquals(1, TeamUtil.getTeamById(EmbeddedDataLoader.CLUB_ID, teamId, ownerToken).getLeaders().size());

      TeamDto teamDTO = TeamUtil.removeLeaderFromTeam(EmbeddedDataLoader.CLUB_ID, leaderId, teamId, ownerToken);
      Assertions.assertEquals(0, teamDTO.getLeaders().size());
      Assertions.assertEquals(0, TeamUtil.getTeamById(EmbeddedDataLoader.CLUB_ID, teamId, ownerToken).getLeaders().size());
    }
  }

  @Test
  void updateTeamMembers() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      List<TeamDto> clubTeams = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, ownerToken);
      String teamId = clubTeams.get(0).getTeamId();
      TeamDto teamDTO = TeamUtil.getTeamById(EmbeddedDataLoader.CLUB_ID, teamId, ownerToken);
      Assertions.assertEquals(3, teamDTO.getMembers().size());
      Assertions.assertEquals(1, teamDTO.getLeaders().size());

      TeamDto updatedTeamDto = TeamUtil.updateTeamMembers(EmbeddedDataLoader.CLUB_ID, teamId, List.of(EmbeddedDataLoader.USER_ID.toString()), ownerToken);
      Set<ClubUserDto> members = updatedTeamDto.getMembers();
      Assertions.assertEquals(1, members.size());
      Assertions.assertEquals(1, updatedTeamDto.getLeaders().size());
      Assertions.assertEquals(EmbeddedDataLoader.USER_ID, members.iterator().next().getUserId());
    }
  }

  @Test
  void updateTeamTest() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String teamName = "Some other name";
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserId mamaId = UserUtil.getUserIdByEmail(EmbeddedDataLoader.MOMMY_EMAIL, context);
      List<TeamDto> teamDtos = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, ownerToken);
      String teamId = teamDtos.iterator().next().getTeamId();

      validateLeaderHasExpectedEmail(EmbeddedDataLoader.POPS_EMAIL, ownerToken, teamId);

      TeamRequestModel teamRequestModel = TeamUtil.createRequestModel(List.of(), List.of(mamaId.toString()), teamName);
      TeamDto teamDTO = TeamUtil.updateTeam(EmbeddedDataLoader.CLUB_ID, teamRequestModel, teamId, ownerToken);
      Assertions.assertEquals(1, teamDTO.getLeaders().size());
      Assertions.assertEquals(0, teamDTO.getMembers().size());
      Assertions.assertNotNull(teamDTO.getDescription());
      Assertions.assertEquals(teamName, teamDTO.getName());
      validateLeaderHasExpectedEmail(EmbeddedDataLoader.MOMMY_EMAIL, ownerToken, teamId);
    }
  }

  @Test
  void userCannotCreateTeamTest() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String userToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TeamRequestModel teamRequestModel = TeamUtil.createRequestModel(List.of(), List.of(), TEAM_NAME);
      try {
        TeamUtil.createTeam(EmbeddedDataLoader.CLUB_ID, teamRequestModel, userToken);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
    }
  }

  private void validateLeaderHasExpectedEmail(String expectedEmail, String token, String teamId) throws JsonProcessingException {
    UserId userId = TeamUtil.getTeamById(EmbeddedDataLoader.CLUB_ID, teamId, token)
        .getLeaders()
        .iterator()
        .next()
        .getUserId();

    Assertions.assertEquals(expectedEmail, UserUtil.getUserEmail(token, EmbeddedDataLoader.CLUB_ID, userId));
  }
}
