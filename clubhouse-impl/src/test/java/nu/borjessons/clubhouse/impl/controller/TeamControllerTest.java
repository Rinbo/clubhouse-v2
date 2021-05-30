package nu.borjessons.clubhouse.impl.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import nu.borjessons.clubhouse.impl.controller.util.TestUtil;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.TeamDTO;
import nu.borjessons.clubhouse.impl.service.TeamService;

class TeamControllerTest {

  @Test
  void getTeam() {
    final TeamService teamService = Mockito.mock(TeamService.class);
    final TeamController teamController = new TeamController(teamService);
    final User principal = TestUtil.getClubUser(TestUtil.USER_1);
    final String teamId = TestUtil.TEAM_1_ID;
    TeamDTO teamDTO = teamController.getTeam(principal, teamId);
    TeamDTO expectedTeamDTO = new TeamDTO(TestUtil.getTeam(teamId));

    Assertions.assertEquals(expectedTeamDTO.getTeamId(), teamDTO.getTeamId());
    Assertions.assertEquals(expectedTeamDTO.getName(), teamDTO.getName());

    Mockito.verifyNoInteractions(teamService);
  }
}