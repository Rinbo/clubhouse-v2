package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

  @PreAuthorize("hasRole('ADMIN')")
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
        .collect(Collectors.toList());
  }

  @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.userId")
  @PostMapping("/clubs/{clubId}/users/{userId}")
  public ClubUserDTO addUserToClub(@PathVariable String clubId, @PathVariable String userId) {
    return clubUserService.addUserToClub(clubId, userId);
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

  /**
   * Principal needs similar functionality to add another parent to his/her children. Needs helper
   * end point to search for a user in the club by email
   * Update 2021-06-20: Consider having a remove and add method since DELETE PUT is not as applicable to a resource that
   * can belong to several entities
   */
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/clubs/{clubId}/users/{userId}/add-children")
  public ClubUserDTO addExistingChildrenToUser(@PathVariable String clubId, @PathVariable String userId, @RequestBody List<String> childrenIds) {
    return clubUserService.addExistingChildrenToUser(userId, clubId, childrenIds);
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
