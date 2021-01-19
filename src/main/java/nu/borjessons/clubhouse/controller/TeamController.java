package nu.borjessons.clubhouse.controller;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.TeamRequestModel;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.ClubRole.Role;
import nu.borjessons.clubhouse.data.Team;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.TeamDTO;
import nu.borjessons.clubhouse.service.TeamService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/teams")
@RequiredArgsConstructor
@RestController
public class TeamController extends AbstractController {

  private final TeamService teamService;

  /*
   * Principal end points
   */

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/principal/{teamId}")
  public TeamDTO getTeam(@PathVariable String teamId) {
    Optional<Team> maybeTeam =
        getPrincipal().getActiveClub().getTeams().stream()
            .filter(team -> team.getTeamId().equals(teamId))
            .findFirst();
    Team team = getOptional(maybeTeam, Team.class, teamId);
    return new TeamDTO(team);
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

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/principal")
  public Set<TeamDTO> getTeams() {
    Set<Team> teams = getPrincipal().getActiveClub().getTeams();
    return teams.stream().map(TeamDTO::new).collect(Collectors.toSet());
  }

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/principal/join")
  public TeamDTO joinTem(@RequestParam String teamId) {
    User principal = getPrincipal();
    Set<Team> teams = principal.getActiveClub().getTeams();
    Optional<Team> maybeTeam =
        teams.stream().filter(team -> team.getTeamId().equals(teamId)).findFirst();
    return teamService.addMemberToTeam(principal, getOptional(maybeTeam, Team.class, teamId));
  }

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/principal/leave")
  public void leaveTeam(@RequestParam String teamId) {
    User principal = getPrincipal();
    Set<Team> teams = principal.getActiveClub().getTeams();
    Optional<Team> maybeTeam =
        teams.stream().filter(team -> team.getTeamId().equals(teamId)).findFirst();
    teamService.removeMemberFromTeam(principal, getOptional(maybeTeam, Team.class, teamId));
  }

  /*
   * Parent end points
   */

  @PreAuthorize("hasRole('PARENT')")
  @PutMapping("/principal/child/add")
  public TeamDTO addChildToTeam(@RequestParam String childId, @RequestParam String teamId) {
    User parent = getPrincipal();
    Club club = parent.getActiveClub();
    Optional<User> maybeChild =
        parent.getChildren().stream()
            .filter(child -> child.getUserId().equals(childId))
            .findFirst();
    Optional<Team> maybeTeam =
        club.getTeams().stream().filter(team -> team.getTeamId().equals(teamId)).findFirst();
    return teamService.addMemberToTeam(
        getOptional(maybeChild, User.class, childId), getOptional(maybeTeam, Team.class, teamId));
  }

  /*
   * Administrator end points
   */

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public TeamDTO createTeam(@RequestBody @Valid TeamRequestModel teamModel) {
    User admin = getPrincipal();
    String clubId = admin.getActiveClubId();
    Set<User> leaders =
        admin.getActiveClub().getUsers().stream()
            .filter(user -> teamModel.getLeaderIds().contains(user.getUserId()))
            .filter(user -> user.getRolesForClub(clubId).contains(Role.LEADER.name()))
            .collect(Collectors.toSet());
    return teamService.createTeam(getPrincipal().getActiveClub(), teamModel, leaders);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{teamId}")
  public TeamDTO updateTeam(
      @RequestBody @Valid TeamRequestModel teamModel, @PathVariable String teamId) {
    User admin = getPrincipal();
    Club activeClub = admin.getActiveClub();
    Set<User> leaders =
        admin.getActiveClub().getUsers().stream()
            .filter(user -> teamModel.getLeaderIds().contains(user.getUserId()))
            .filter(
                user -> user.getRolesForClub(activeClub.getClubId()).contains(Role.LEADER.name()))
            .collect(Collectors.toSet());
    return teamService.updateTeam(activeClub, teamId, teamModel, leaders);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/remove-leader")
  public TeamDTO removeLeader(@RequestParam String userId, @RequestParam String teamId) {
    User admin = getPrincipal();
    Club activeClub = admin.getActiveClub();
    Team team =
        activeClub.getTeams().stream()
            .filter(t -> t.getTeamId().equals(teamId))
            .findFirst()
            .orElseThrow();
    User leader =
        admin.getActiveClub().getUsers().stream()
            .filter(user -> user.getUserId().equals(userId))
            .filter(
                user -> user.getRolesForClub(activeClub.getClubId()).contains(Role.LEADER.name()))
            .findFirst()
            .orElseThrow();
    return teamService.removeLeaderFromTeam(leader, team);
  }
}
