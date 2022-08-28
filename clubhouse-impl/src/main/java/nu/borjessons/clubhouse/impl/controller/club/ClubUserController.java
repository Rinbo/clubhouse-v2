package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.UpcomingTrainingEvent;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.security.resource.authorization.UserResourceAuthorization;
import nu.borjessons.clubhouse.impl.service.ClubUserService;
import nu.borjessons.clubhouse.impl.service.TrainingEventService;

@RequiredArgsConstructor
@RequestMapping("/clubs/{clubId}")
@RestController
public class ClubUserController {
  private final ClubUserService clubUserService;
  private final TrainingEventService trainingEventService;
  private final UserResourceAuthorization userResourceAuthorization;

  @PreAuthorize("hasRole('USER') and #userId == authentication.principal.userId")
  @PutMapping("/users/{userId}/activate-club-children")
  public ClubUserDto activateClubChildren(@PathVariable String clubId, @PathVariable UserId userId, @RequestBody List<UserId> childrenIds) {
    userResourceAuthorization.validateIsChildrenOfUser(userId, childrenIds);
    return clubUserService.activateClubChildren(clubId, userId, childrenIds);
  }

  @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
  @PostMapping("/users/{userId}")
  public ClubUserDto addUserToClub(@PathVariable String clubId, @PathVariable UserId userId, @RequestBody List<String> childrenIds) {
    return clubUserService.addUserToClub(clubId, userId, childrenIds.stream().map(UserId::new).toList());
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/principal")
  public ClubUserDto getClubUserPrincipal(@AuthenticationPrincipal User principal, @PathVariable String clubId) {
    return clubUserService.getClubUser(clubId, principal.getUserId());
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/users/{userId}/email")
  public ResponseEntity<String> getEmail(@PathVariable String clubId, @PathVariable UserId userId) {
    return ResponseEntity.ok(userResourceAuthorization.getUserEmail(userId));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/leaders")
  public Collection<ClubUserDto> getLeaders(@PathVariable String clubId) {
    return clubUserService.getLeaders(clubId);
  }

  @PreAuthorize("hasRole('LEADER')")
  @GetMapping("/principal/upcoming-training")
  public List<UpcomingTrainingEvent> getUpcomingTrainingEvents(@AuthenticationPrincipal User principal, @PathVariable String clubId, String browserTime) {
    return trainingEventService.getUpcomingTrainingEvents(principal.getId(), clubId);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/users/{userId}")
  public ClubUserDto getUser(@PathVariable String clubId, @PathVariable UserId userId) {
    return clubUserService.getClubUser(clubId, userId);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping(path = "/users")
  public Collection<ClubUserDto> getUsers(@PathVariable String clubId) {
    return clubUserService.getClubUsers(clubId);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping(path = "/basic-users")
  public Collection<BaseUserRecord> getUsersBasic(@PathVariable String clubId) {
    return clubUserService.getClubUsersBasic(clubId);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping(path = "/users/subset")
  public Collection<BaseUserRecord> getUsersSubset(@PathVariable String clubId, @RequestParam List<UserId> userIds) {
    return clubUserService.getClubUsersSubset(clubId, userIds);
  }

  @PreAuthorize("hasRole('USER') and #userId == authentication.principal.userId")
  @PutMapping("/users/{userId}/remove-club-children")
  public ClubUserDto removeClubChildren(@PathVariable String clubId, @PathVariable UserId userId, @RequestBody List<String> childrenIds) {
    return clubUserService.removeClubChildren(clubId, userId, childrenIds.stream().map(UserId::new).toList());
  }

  @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
  @DeleteMapping("/users/{userId}")
  public void removeUserFromClub(@PathVariable String clubId, @PathVariable UserId userId) {
    clubUserService.removeUserFromClub(userId, clubId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/users/{userId}")
  public ClubUserDto updateUser(@PathVariable String clubId, @PathVariable UserId userId, @Valid @RequestBody AdminUpdateUserModel userDetails) {
    return clubUserService.updateUser(userId, clubId, userDetails);
  }
}
