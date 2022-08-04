package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.TeamDto;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;
import nu.borjessons.clubhouse.impl.security.resource.authorization.UserResourceAuthorization;
import nu.borjessons.clubhouse.impl.service.ClubService;
import nu.borjessons.clubhouse.impl.service.TeamService;

@RequiredArgsConstructor
@RestController
public class ClubTeamController {
  private final ClubService clubService;
  private final TeamService teamService;
  private final UserResourceAuthorization userResourceAuthorization;

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/clubs/{clubId}/teams/{teamId}/add-child")
  public TeamDto addChildrenToTeam(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId,
      @RequestParam UserId childId) {
    userResourceAuthorization.validateIsChildOfUser(childId, principal.getUserId());
    return teamService.addMemberToTeam(clubId, teamId, childId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping(path = "/clubs/{clubId}/teams")
  public TeamDto createTeam(@PathVariable String clubId, @RequestBody @Valid TeamRequestModel teamModel) {
    return teamService.createTeam(clubId, teamModel);
  }

  // TODO Need one for getting leader teams and children teams.
  @PreAuthorize("hasRole('USER')")
  @GetMapping("/clubs/{clubId}/my-teams")
  public Set<TeamDto> getMyTeams(@AuthenticationPrincipal User principal, @PathVariable String clubId) {
    return teamService.getTeamsByUserId(clubId, principal.getUserId());
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/clubs/{clubId}/teams/{teamId}")
  public TeamDto getTeam(@PathVariable String clubId, @PathVariable String teamId) {
    return teamService.getTeam(teamId);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/clubs/{clubId}/teams")
  public Collection<TeamDto> getTeams(@PathVariable String clubId) {
    return teamService.getClubTeams(clubId);
  }

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/clubs/{clubId}/teams/{teamId}/join")
  public TeamDto joinTeam(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId) {
    return teamService.addMemberToTeam(clubId, teamId, principal.getUserId());
  }

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/clubs/{clubId}/teams/{teamId}/leave")
  public void leaveTeam(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String teamId) {
    teamService.removeMemberFromTeam(clubId, teamId, principal.getUserId());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/clubs/{clubId}/teams/{teamId}/remove-leader")
  public TeamDto removeLeader(@PathVariable String clubId, @PathVariable String teamId, @RequestParam UserId leaderId) {
    return teamService.removeLeaderFromTeam(clubId, teamId, leaderId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/clubs/{clubId}/teams/{teamId}")
  public TeamDto updateTeam(@PathVariable String clubId, @PathVariable String teamId, @RequestBody @Valid TeamRequestModel teamModel) {
    return teamService.updateTeam(clubId, teamId, teamModel);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/clubs/{clubId}/teams/{teamId}")
  public ResponseEntity<String> updateTeam(@PathVariable String clubId, @PathVariable String teamId) {
    teamService.deleteTeam(teamId);
    return ResponseEntity.ok("Team deleted");
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/clubs/{clubId}/teams/{teamId}/members")
  public TeamDto updateTeamMembers(@PathVariable String clubId, @PathVariable String teamId, @RequestBody List<String> userIds) {
    return teamService.updateTeamMembers(clubId, teamId, userIds.stream().map(UserId::new).toList());
  }
}
