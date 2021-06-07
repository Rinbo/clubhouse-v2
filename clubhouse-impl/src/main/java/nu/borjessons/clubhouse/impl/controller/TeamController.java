package nu.borjessons.clubhouse.impl.controller;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.controller.model.request.TeamRequestModel;
import nu.borjessons.clubhouse.impl.controller.model.request.UpdateTeamMembersRequestModel;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubRole.Role;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.TeamDTO;
import nu.borjessons.clubhouse.impl.service.TeamService;

@RequiredArgsConstructor
@RestController
public class TeamController {
  private final TeamService teamService;

  @PutMapping("/teams/{teamId}/add-child")
  public TeamDTO addChildToTeam(@AuthenticationPrincipal User principal, @RequestParam String childId, @RequestParam String teamId) {
    // TODO Evaluate checking for Role.PARENT

    User requestedChild = principal
        .getChildren()
        .stream()
        .filter(child -> child.getUserId().equals(childId))
        .findFirst()
        .orElseThrow();

    Team requestedTeam = principal
        .getClubs()
        .stream()
        .map(Club::getTeams)
        .flatMap(Set::stream)
        .filter(team -> team.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();

    return teamService.addMemberToTeam(requestedChild, requestedTeam);
  }

  @PostMapping(path = "/clubs/{clubId}/teams")
  public TeamDTO createTeam(@AuthenticationPrincipal User principal, @PathVariable String clubId, @RequestBody @Valid TeamRequestModel teamModel) {
    // TODO require Role.ADMIN

    Club club = principal
        .getClubByClubId(clubId)
        .orElseThrow();

    Set<User> leaders = club
        .getUsers()
        .stream()
        .filter(user -> teamModel.getLeaderIds().contains(user.getUserId()))
        .filter(user -> user.getRolesForClub(clubId).contains(Role.LEADER.name()))
        .collect(Collectors.toSet());

    return teamService.createTeam(club, teamModel, leaders);
  }

  @GetMapping("/clubs/{clubId}/my-teams")
  public Set<TeamDTO> getMyTeams(@AuthenticationPrincipal User principal, @PathVariable String clubId) {
    // TODO require Role.USER
    Set<Team> teams = principal.getClubByClubId(clubId).orElseThrow().getTeams();
    return teams.stream()
        .filter(team -> team.getMembers().contains(principal))
        .map(TeamDTO::new)
        .collect(Collectors.toSet());
  }

  @GetMapping("teams/{teamId}")
  public TeamDTO getTeam(@AuthenticationPrincipal User principal, @PathVariable String teamId) {
    Team myTeam = principal
        .getClubs()
        .stream()
        .map(Club::getTeams)
        .flatMap(Set::stream)
        .filter(team -> team.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();
    return new TeamDTO(myTeam);
  }

  @GetMapping("/clubs/{clubId}/teams")
  public Set<TeamDTO> getTeams(@AuthenticationPrincipal User principal, @PathVariable String clubId) {
    // TODO require Role.USER
    return principal.getClubByClubId(clubId)
        .orElseThrow()
        .getTeams()
        .stream()
        .map(TeamDTO::new)
        .collect(Collectors.toSet());
  }

  /*
   * Parent end points
   */

  @PutMapping("/clubs/{clubId}/teams/{teamId}/join")
  public TeamDTO joinTem(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId) {
    // TODO require Role.USER
    Team selectedTeam = principal.getClubByClubId(clubId)
        .orElseThrow()
        .getTeams()
        .stream()
        .filter(team -> team.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();
    return teamService.addMemberToTeam(principal, selectedTeam);
  }

  /*
   * Administrator end points
   */

  @PutMapping("/clubs/{clubId}/teams/{teamId}/leave")
  public void leaveTeam(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId) {
    // TODO require Role.USER
    Team selectedTeam = principal.getClubByClubId(clubId)
        .orElseThrow()
        .getTeams()
        .stream()
        .filter(team -> team.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();
    teamService.removeMemberFromTeam(principal, selectedTeam);
  }

  @PutMapping("/clubs/{clubId}/teams/{teamId}/remove-leader")
  public TeamDTO removeLeader(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId, @RequestParam String userId) {
    // TODO require Role.ADMIN

    Club club = principal.getClubByClubId(clubId).orElseThrow();

    Team selectedTeam = club.getTeams()
        .stream()
        .filter(team -> team.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();

    User leader = club
        .getUsers()
        .stream()
        .filter(user -> user.getUserId().equals(userId))
        .filter(user -> user.getRolesForClub(club.getClubId()).contains(Role.LEADER.name()))
        .findFirst()
        .orElseThrow();

    return teamService.removeLeaderFromTeam(leader, selectedTeam);
  }

  @PutMapping("/clubs/{clubId}/teams/{teamId}")
  public TeamDTO updateTeam(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId,
      @RequestBody @Valid TeamRequestModel teamModel) {
    // TODO require Role.ADMIN
    Club club = principal.getClubByClubId(clubId).orElseThrow();
    Set<User> leaders = club
        .getUsers()
        .stream()
        .filter(user -> teamModel.getLeaderIds().contains(user.getUserId()))
        .filter(user -> user.getRolesForClub(club.getClubId()).contains(Role.LEADER.name()))
        .collect(Collectors.toSet());

    return teamService.updateTeam(club, teamId, teamModel, leaders);
  }

  @PutMapping("/clubs/{clubId}/teams/members")
  public TeamDTO updateTeamMembers(@AuthenticationPrincipal User principal, @PathVariable String clubId,
      @Valid @RequestBody UpdateTeamMembersRequestModel requestModel) {
    // TODO require Role.ADMIN
    Club club = principal.getClubByClubId(clubId).orElseThrow();
    return teamService.updateTeamMembers(club, requestModel.getTeamId(), requestModel.getMemberIds());
  }
}