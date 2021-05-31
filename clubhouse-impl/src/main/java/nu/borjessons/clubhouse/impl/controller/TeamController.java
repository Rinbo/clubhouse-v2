package nu.borjessons.clubhouse.impl.controller;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

@RequestMapping("/teams")
@RequiredArgsConstructor
@RestController
public class TeamController extends AbstractController {

  private final TeamService teamService;

  /*
   * Principal end points
   */

  @PutMapping("/principal/child/add")
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

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public TeamDTO createTeam(@RequestBody @Valid TeamRequestModel teamModel) {
    User admin = getPrincipal();
    String clubId = admin.getActiveClubId();
    Set<User> leaders = admin
        .getActiveClub()
        .getUsers()
        .stream()
        .filter(user -> teamModel.getLeaderIds().contains(user.getUserId()))
        .filter(user -> user.getRolesForClub(clubId).contains(Role.LEADER.name()))
        .collect(Collectors.toSet());

    return teamService.createTeam(getPrincipal().getActiveClub(), teamModel, leaders);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/principal/my-teams")
  public Set<TeamDTO> getMyTeams() {
    User user = getPrincipal();
    Set<Team> teams = user.getActiveClub().getTeams();
    return teams.stream()
        .filter(team -> team.getMembers().contains(user))
        .map(TeamDTO::new)
        .collect(Collectors.toSet());
  }

  @GetMapping("/principal/{teamId}")
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

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/principal")
  public Set<TeamDTO> getTeams() {
    return getPrincipal()
        .getActiveClub()
        .getTeams()
        .stream()
        .map(TeamDTO::new)
        .collect(Collectors.toSet());
  }

  /*
   * Parent end points
   */

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/principal/join")
  public TeamDTO joinTem(@RequestParam String teamId) {
    User principal = getPrincipal();
    Set<Team> teams = principal.getActiveClub().getTeams();
    Optional<Team> optional = teams.stream().filter(team -> team.getTeamId().equals(teamId)).findFirst();
    return teamService.addMemberToTeam(principal, optional.orElseThrow());
  }

  /*
   * Administrator end points
   */

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/principal/leave")
  public void leaveTeam(@RequestParam String teamId) {
    Optional<Team> optional = getPrincipal()
        .getActiveClub()
        .getTeams()
        .stream()
        .filter(team -> team.getTeamId().equals(teamId))
        .findFirst();
    teamService.removeMemberFromTeam(getPrincipal(), optional.orElseThrow());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/remove-leader")
  public TeamDTO removeLeader(@RequestParam String userId, @RequestParam String teamId) {
    User admin = getPrincipal();
    Club activeClub = admin.getActiveClub();
    Team team = activeClub
        .getTeams()
        .stream()
        .filter(t -> t.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();

    User leader = activeClub
        .getUsers()
        .stream()
        .filter(user -> user.getUserId().equals(userId))
        .filter(user -> user.getRolesForClub(activeClub.getClubId()).contains(Role.LEADER.name()))
        .findFirst()
        .orElseThrow();

    return teamService.removeLeaderFromTeam(leader, team);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{teamId}")
  public TeamDTO updateTeam(@RequestBody @Valid TeamRequestModel teamModel, @PathVariable String teamId) {
    User admin = getPrincipal();
    Club activeClub = admin.getActiveClub();
    Set<User> leaders = activeClub
        .getUsers()
        .stream()
        .filter(user -> teamModel.getLeaderIds().contains(user.getUserId()))
        .filter(user -> user.getRolesForClub(activeClub.getClubId()).contains(Role.LEADER.name()))
        .collect(Collectors.toSet());

    return teamService.updateTeam(activeClub, teamId, teamModel, leaders);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/members")
  public TeamDTO updateTeamMembers(@Valid @RequestBody UpdateTeamMembersRequestModel requestModel) {
    return teamService.updateTeamMembers(getPrincipal().getActiveClub(), requestModel.getTeamId(), requestModel.getMemberIds());
  }
}