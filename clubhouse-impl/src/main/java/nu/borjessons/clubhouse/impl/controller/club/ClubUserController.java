package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

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
import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.service.ClubService;
import nu.borjessons.clubhouse.impl.service.ClubUserService;

@RequiredArgsConstructor
@RestController
public class ClubUserController {
  private final ClubService clubService;
  private final ClubUserService clubUserService;

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/clubs/{clubId}/principal")
  public ClubUserDTO getClubUserPrincipal(@AuthenticationPrincipal User principal, @PathVariable String clubId) {
    return clubUserService.getClubUser(clubId, principal.getUserId());
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/clubs/{clubId}/users/{userId}")
  public ClubUserDTO getUser(@PathVariable String clubId, @PathVariable String userId) {
    return clubUserService.getClubUser(clubId, userId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/clubs/{clubId}/users/age-range")
  public Collection<ClubUserDTO> getUsersByAgeRange(@PathVariable String clubId, @RequestParam int minAge, @RequestParam int maxAge) {
    return clubService
        .getClubByClubId(clubId)
        .getClubUsers()
        .stream()
        .filter(clubUser -> clubUser.getUser().getAge() >= minAge && clubUser.getUser().getAge() <= maxAge)
        .map(ClubUserDTO::new)
        .toList();
  }

  @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
  @PostMapping("/clubs/{clubId}/users/{userId}")
  public ClubUserDTO addUserToClub(@PathVariable String clubId, @PathVariable String userId, @RequestBody List<String> childrenIds) {
    return clubUserService.addUserToClub(clubId, userId, childrenIds);
  }

  @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
  @DeleteMapping("/clubs/{clubId}/users/{userId}")
  public void removeUserFromClub(@PathVariable String clubId, @PathVariable String userId) {
    clubUserService.removeUserFromClub(userId, clubId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/clubs/{clubId}/users/{userId}")
  public ClubUserDTO updateUser(@PathVariable String clubId, @PathVariable String userId, @Valid @RequestBody AdminUpdateUserModel userDetails) {
    return clubUserService.updateUser(userId, clubId, userDetails);
  }

  //TODO Give admin access to this method as well
  @PreAuthorize("hasRole('USER') and #userId == authentication.principal.userId")
  @PutMapping("/clubs/{clubId}/users/{userId}/activate-club-children")
  public ClubUserDTO activateClubChildren(@PathVariable String clubId, @PathVariable String userId, @RequestBody List<String> childrenIds) {
    return clubUserService.activateClubChildren(clubId, userId, childrenIds);
  }

  //TODO Give admin access to this method as well
  @PreAuthorize("hasRole('USER') and #userId == authentication.principal.userId")
  @PutMapping("/clubs/{clubId}/users/{userId}/remove-club-children")
  public ClubUserDTO removeClubChildren(@PathVariable String clubId, @PathVariable String userId, @RequestBody List<String> childrenIds) {
    return clubUserService.removeClubChildren(clubId, userId, childrenIds);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/clubs/{clubId}/leaders")
  public Collection<ClubUserDTO> getLeaders(@PathVariable String clubId) {
    return clubUserService.getLeaders(clubId);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping(path = "/clubs/{clubId}/users")
  public Collection<ClubUserDTO> getUsers(@PathVariable String clubId) {
    return clubUserService.getClubUsers(clubId);
  }
}
