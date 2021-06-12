package nu.borjessons.clubhouse.impl.controller.club;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubRole;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.TeamDTO;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;
import nu.borjessons.clubhouse.impl.service.TeamService;

@RequiredArgsConstructor
@RestController
public class ClubTeamController {
  private final TeamService teamService;

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/clubs/{clubId}/teams/{teamId}/add-child")
  public TeamDTO addChildToTeam(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId,
      @RequestParam String childId) {
    User requestedChild = principal
        .getChildren()
        .stream()
        .filter(child -> child.getUserId().equals(childId))
        .findFirst()
        .orElseThrow();

    Team requestedTeam = principal
        .getClubByClubId(clubId)
        .stream()
        .map(Club::getTeams)
        .flatMap(Set::stream)
        .filter(team -> team.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();

    return teamService.addMemberToTeam(requestedChild, requestedTeam);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(path = "/clubs/{clubId}/teams")
  public TeamDTO createTeam(@AuthenticationPrincipal User principal, @PathVariable String clubId, @RequestBody @Valid TeamRequestModel teamModel) {
    Club club = principal
        .getClubByClubId(clubId)
        .orElseThrow();

    Set<User> leaders = club
        .getUsers()
        .stream()
        .filter(user -> teamModel.getLeaderIds().contains(user.getUserId()))
        .filter(user -> user.getRolesForClub(clubId).contains(ClubRole.RoleTemp.LEADER.name()))
        .collect(Collectors.toSet());

    return teamService.createTeam(club, teamModel, leaders);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/clubs/{clubId}/my-teams")
  public Set<TeamDTO> getMyTeams(@AuthenticationPrincipal User principal, @PathVariable String clubId) {
    return principal.getClubByClubId(clubId)
        .stream()
        .map(Club::getTeams)
        .flatMap(Set::stream)
        .filter(team -> team.getMembers().contains(principal))
        .map(TeamDTO::new)
        .collect(Collectors.toSet());
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/clubs/{clubId]/teams/{teamId}")
  public TeamDTO getTeam(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId) {
    Team myTeam = principal
        .getClubByClubId(clubId)
        .stream()
        .map(Club::getTeams)
        .flatMap(Set::stream)
        .filter(team -> team.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();
    return new TeamDTO(myTeam);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/clubs/{clubId}/teams")
  public Set<TeamDTO> getTeams(@AuthenticationPrincipal User principal, @PathVariable String clubId) {
    return principal.getClubByClubId(clubId)
        .orElseThrow()
        .getTeams()
        .stream()
        .map(TeamDTO::new)
        .collect(Collectors.toSet());
  }

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/clubs/{clubId}/teams/{teamId}/join")
  public TeamDTO joinTem(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId) {
    Team selectedTeam = principal.getClubByClubId(clubId)
        .stream()
        .map(Club::getTeams)
        .flatMap(Set::stream)
        .filter(team -> team.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();
    return teamService.addMemberToTeam(principal, selectedTeam);
  }

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/clubs/{clubId}/teams/{teamId}/leave")
  public void leaveTeam(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId) {
    Team selectedTeam = principal.getClubByClubId(clubId)
        .stream()
        .map(Club::getTeams)
        .flatMap(Set::stream)
        .filter(team -> team.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();
    teamService.removeMemberFromTeam(principal, selectedTeam);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/clubs/{clubId}/teams/{teamId}/remove-leader")
  public TeamDTO removeLeader(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId, @RequestParam String userId) {
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
        .filter(user -> user.getRolesForClub(clubId).contains(ClubRole.RoleTemp.LEADER.name()))
        .findFirst()
        .orElseThrow();

    return teamService.removeLeaderFromTeam(leader, selectedTeam);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/clubs/{clubId}/teams/{teamId}")
  public TeamDTO updateTeam(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId,
      @RequestBody @Valid TeamRequestModel teamModel) {
    Club club = principal.getClubByClubId(clubId).orElseThrow();
    Set<User> leaders = club
        .getUsers()
        .stream()
        .filter(user -> teamModel.getLeaderIds().contains(user.getUserId()))
        .filter(user -> user.getRolesForClub(club.getClubId()).contains(ClubRole.RoleTemp.LEADER.name()))
        .collect(Collectors.toSet());

    return teamService.updateTeam(club, teamId, teamModel, leaders);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/clubs/{clubId}/teams/{teamId}/members")
  public TeamDTO updateTeamMembers(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId, Set<String> memberIds) {
    Club club = principal.getClubByClubId(clubId).orElseThrow();

    return teamService.updateTeamMembers(club, teamId, memberIds);
  }
}
