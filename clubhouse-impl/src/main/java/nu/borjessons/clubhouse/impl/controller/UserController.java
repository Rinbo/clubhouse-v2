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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController extends AbstractController {

  private final UserService userService;

  @PreAuthorize("hasRole('USER')")
  @GetMapping
  public Set<UserDTO> getUsers() {
    Club activeClub = getPrincipal().getActiveClub();
    return activeClub
        .getUsers()
        .stream()
        .map(user -> new UserDTO(user, activeClub.getClubId()))
        .collect(Collectors.toSet());
  }

  /*
   * Principal routes
   */

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/principal")
  public UserDTO getSelf() {
    User user = getPrincipal();
    return new UserDTO(user, user.getActiveClubId());
  }

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/principal")
  public UserDTO updateSelf(@RequestBody UpdateUserModel userDetails) {
    User user = getPrincipal();
    return userService.updateUser(user, user.getActiveClub(), userDetails);
  }

  @DeleteMapping("/principal")
  public void deleteSelf() {
    User user = getPrincipal();
    userService.deleteUser(user);
  }

  @PreAuthorize("hasRole('USER')")
  @PutMapping("/principal/leave-club")
  public void leaveClub() {
    User user = getPrincipal();
    userService.removeUserFromClub(user, user.getActiveClub());
  }

  @PutMapping("/principal/switch-club")
  public UserDTO switchClub(@RequestParam String clubId) {
    User user = getPrincipal();
    Optional<Club> optional = user.getClubs().stream().filter(club -> club.getClubId().equals(clubId)).findFirst();
    return userService.switchClub(user, optional.orElseThrow());
  }

  @PutMapping("/principal/join-club")
  public UserDTO joinClub(@RequestParam String clubId) {
    User user = getPrincipal();
    if (user.getClubs().stream().anyMatch(club -> club.getClubId().equals(clubId))) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already a member of this club");
    }
    return userService.joinClub(user, clubId);
  }

  @GetMapping("/roles")
  public ClubRole.Role[] getRolesNames() {
    return ClubRole.Role.values();
  }

  /*
   * Administrator routes
   */

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{userId}")
  public UserDTO getUser(@PathVariable String userId) {
    Club club = getPrincipal().getActiveClub();
    return new UserDTO(club.getUser(userId), club.getClubId());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/age-range")
  public Set<BaseUserDTO> getUsersByAgeRange(@RequestParam int minAge, @RequestParam int maxAge) {
    Club club = getPrincipal().getActiveClub();
    return club.getUsers()
        .stream()
        .filter(user -> user.getAge() <= maxAge && user.getAge() >= minAge)
        .map(BaseUserDTO::new)
        .collect(Collectors.toSet());
  }

  //TODO: Horrible execution. Rething deeply
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{userId}")
  public UserDTO updateUser(@PathVariable String userId, @RequestBody @Valid AdminUpdateUserModel userDetails) {
    Club club = getPrincipal().getActiveClub();
    User user = club.getUser(userId);
    userService.updateUser(user, club, userDetails);
    return userService.updateUserRoles(user, club, userDetails.getRoles());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/roles/{userId}")
  public UserDTO updateUser(@PathVariable String userId, @RequestBody Set<ClubRole.Role> roles) {
    Club club = getPrincipal().getActiveClub();
    return userService.updateUserRoles(club.getUser(userId), club, roles);
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
