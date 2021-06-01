package nu.borjessons.clubhouse.impl.controller;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import nu.borjessons.clubhouse.impl.controller.model.request.TeamRequestModel;
import nu.borjessons.clubhouse.impl.controller.util.TestUtil;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.TeamDTO;
import nu.borjessons.clubhouse.impl.service.TeamService;

class TeamControllerTest {

  @Test
  void createTeamInClub() {
    final TeamService teamService = Mockito.mock(TeamService.class);
    final User principal = TestUtil.getClubUser(TestUtil.OWNER_1_ID);
    final TeamController teamController = new TeamController(teamService);

    final TeamRequestModel teamRequestModel = new TeamRequestModel();
    teamRequestModel.setName("New Team");
    teamRequestModel.setMinAge(6);
    teamRequestModel.setMaxAge(12);
    teamRequestModel.setLeaderIds(Set.of(principal.getUserId()));

    teamController.createTeam(principal, TestUtil.CLUB_1_ID, teamRequestModel);

    Mockito.verify(teamService).createTeam(TestUtil.CLUB1, teamRequestModel, Set.of(principal));
    Mockito.verifyNoMoreInteractions(teamService);
  }

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

  @Test
  void principalAddsChildToTeam() {
    final TeamService teamService = Mockito.mock(TeamService.class);
    final TeamController teamController = new TeamController(teamService);
    final User dad = TestUtil.getClubUser(TestUtil.PARENT_1_ID);
    final String teamId = TestUtil.TEAM_1_ID;
    final String childId = TestUtil.CHILD_1_ID;

    teamController.addChildToTeam(dad, childId, teamId);
    Mockito.verify(teamService).addMemberToTeam(TestUtil.getClubUser(childId), TestUtil.getTeam(teamId));
  }
}