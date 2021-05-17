package nu.borjessons.clubhouse.impl.controller;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.controller.model.request.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.controller.model.request.UpdateUserModel;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubRole;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.BaseUserDTO;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.service.UserService;
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

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController extends AbstractController {

  private final UserService userService;

  /*
   * Principal routes
   */

  @GetMapping("/principal")
  public UserDTO getSelf(@AuthenticationPrincipal User principal) {
    return UserDTO.create(principal);
  }

  @PutMapping("/principal")
  public UserDTO updateSelf(@RequestBody UpdateUserModel userDetails, @AuthenticationPrincipal User principal) {
    return userService.updateUser(principal, userDetails);
  }

  @DeleteMapping("/principal")
  public void deleteSelf() {
    User user = getPrincipal();
    userService.deleteUser(user);
  }

  @GetMapping("/roles")
  public ClubRole.Role[] getRolesNames() {
    return ClubRole.Role.values();
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/club/{clubId}/roles")
  public Set<String> getActiveClub(@PathVariable String clubId) {
    return getPrincipal().getRolesForClub(clubId);
  }

  /*
   * Administrator routes
   */

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/club/{clubId}/user/{userId}")
  public UserDTO getUser(@PathVariable String clubId, @PathVariable String userId, @AuthenticationPrincipal User principal) {
    // TODO require role admin
    User user = principal
        .getClubByClubId(clubId)
        .getUser(userId);
    return UserDTO.create(user);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/club/{clubId}/age-range")
  public Set<BaseUserDTO> getUsersByAgeRange(@PathVariable String clubId, @RequestParam int minAge, @RequestParam int maxAge,
                                             @AuthenticationPrincipal User principal) {
    return principal
        .getClubByClubId(clubId)
        .getUsers()
        .stream()
        .filter(user -> user.getAge() <= maxAge && user.getAge() >= minAge)
        .map(BaseUserDTO::new)
        .collect(Collectors.toSet());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{userId}")
  public UserDTO updateUser(@PathVariable String userId, @RequestBody @Valid AdminUpdateUserModel userDetails) {
    Club club = getPrincipal().getActiveClub();
    User user = club.getUser(userId);
    return userService.updateUser(user, club, userDetails);
  }

  // Principal needs similar functionality to add another parent to his/her children. Needs helper
  // end point to search for a user in the club by email
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/children/{userId}")
  public UserDTO updateUserChildren(@PathVariable String userId, @RequestBody Set<String> childrenIds) {
    Club club = getPrincipal().getActiveClub();
    User parent = club.getUser(userId);
    Set<User> clubChildren = club.getManagedUsers();
    Set<User> validatedChildren = clubChildren.stream()
        .filter(child -> childrenIds.contains(child.getUserId()))
        .collect(Collectors.toSet());
    return userService.updateUserChildren(parent, validatedChildren, club);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/remove/{userId}")
  public void removeUserFromClub(@PathVariable String userId) {
    Club club = getPrincipal().getActiveClub();
    User user = club.getUser(userId);
    userService.removeUserFromClub(user, club);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/leaders")
  public Set<BaseUserDTO> getLeaders() {
    User admin = getPrincipal();
    String clubId = admin.getActiveClubId();
    return admin.getActiveClub()
        .getUsers()
        .stream()
        .filter(user -> user.getRolesForClub(clubId).contains(ClubRole.Role.LEADER.name()))
        .map(BaseUserDTO::new)
        .collect(Collectors.toSet());
  }
}
