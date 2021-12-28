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
      TeamDto teamDTO = TeamUtil.createTeam(EmbeddedDataLoader.CLUB_ID, teamRequestModel, ownerToken);
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
      List<TeamDto> teamDtos = TeamUtil.getMyTeams(EmbeddedDataLoader.CLUB_ID, token);
      Assertions.assertEquals(1, teamDtos.size());
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
      final List<TeamDto> clubTeams = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, token);
      final String teamId = clubTeams.get(0).getTeamId();

      TeamDto teamDTO = TeamUtil.getTeamById(EmbeddedDataLoader.CLUB_ID, teamId, token);
      Assertions.assertEquals(teamId, teamDTO.getTeamId());
    }
  }

  @Test
  void joinAndLeaveTeam() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      TeamRequestModel teamRequestModel = TeamUtil.createRequestModel(List.of(), TEAM_NAME);
      final TeamDto newTeamDto = TeamUtil.createTeam(EmbeddedDataLoader.CLUB_ID, teamRequestModel, ownerToken);

      final String token = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final String teamId = newTeamDto.getTeamId();
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
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final ClubUserDto leaderDTO = UserUtil.getUserIdByEmail(UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken), EmbeddedDataLoader.POPS_EMAIL);
      final List<TeamDto> teamDtos = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, ownerToken);
      final String teamId = teamDtos.get(0).getTeamId();

      Assertions.assertEquals(1, TeamUtil.getTeamById(EmbeddedDataLoader.CLUB_ID, teamId, ownerToken).getLeaders().size());

      TeamDto teamDTO = TeamUtil.removeLeaderFromTeam(EmbeddedDataLoader.CLUB_ID, new UserId(leaderDTO.getUserId()), teamId, ownerToken);
      Assertions.assertEquals(0, teamDTO.getLeaders().size());
      Assertions.assertEquals(0, TeamUtil.getTeamById(EmbeddedDataLoader.CLUB_ID, teamId, ownerToken).getLeaders().size());
    }
  }

  @Test
  void updateTeamTest() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String teamName = "Some other name";
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final ClubUserDto mamaClubUser = UserUtil.getUserIdByEmail(UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken), EmbeddedDataLoader.MOMMY_EMAIL);
      final List<TeamDto> teamDtos = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, ownerToken);
      final String teamId = teamDtos.get(0).getTeamId();

      validateLeaderHasExpectedEmail(EmbeddedDataLoader.POPS_EMAIL, ownerToken, teamId);

      TeamRequestModel teamRequestModel = TeamUtil.createRequestModel(List.of(mamaClubUser.getUserId()), teamName);
      TeamDto teamDTO = TeamUtil.updateTeam(EmbeddedDataLoader.CLUB_ID, teamRequestModel, teamId, ownerToken);
      Assertions.assertEquals(1, teamDTO.getLeaders().size());
      Assertions.assertEquals(teamName, teamDTO.getName());
      validateLeaderHasExpectedEmail(EmbeddedDataLoader.MOMMY_EMAIL, ownerToken, teamId);
    }
  }

  @Test
  void updateTeamMembers() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<TeamDto> clubTeams = TeamUtil.getClubTeams(EmbeddedDataLoader.CLUB_ID, ownerToken);
      final String teamId = clubTeams.get(0).getTeamId();
      final TeamDto teamDTO = TeamUtil.getTeamById(EmbeddedDataLoader.CLUB_ID, teamId, ownerToken);
      Assertions.assertEquals(3, teamDTO.getMembers().size());
      Assertions.assertEquals(1, teamDTO.getLeaders().size());

      TeamDto updatedTeamDto = TeamUtil.updateTeamMembers(EmbeddedDataLoader.CLUB_ID, teamId, List.of(EmbeddedDataLoader.USER_ID.toString()), ownerToken);
      Set<ClubUserDto> members = updatedTeamDto.getMembers();
      Assertions.assertEquals(1, members.size());
      Assertions.assertEquals(1, updatedTeamDto.getLeaders().size());
      Assertions.assertEquals(EmbeddedDataLoader.USER_EMAIL, members.iterator().next().getEmail());
    }
  }

  private void validateLeaderHasExpectedEmail(String expectedEmail, String token, String teamId) throws JsonProcessingException {
    Assertions.assertEquals(expectedEmail, TeamUtil.getTeamById(EmbeddedDataLoader.CLUB_ID, teamId, token)
        .getLeaders()
        .iterator()
        .next()
        .getEmail());
  }
}
