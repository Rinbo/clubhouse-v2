package nu.borjessons.clubhouse.impl.controller;

import java.util.Set;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.controller.model.request.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.controller.model.request.UpdateUserModel;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubRole;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.BaseUserDTO;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.service.UserService;

/**
 * TODO should I have a separate controller for leaders, children?
 * Should leaders be part of teamController?
 * Should UserController be split into ClubUserController for all endpoints that begin with /clubs/{clubId} ?
 * What is the memory footprint of eagerly loading the entire user object on each request?
 * How many sql-queries are made?
 * Ideally, access rights and roles should be sorted out very early so that one could then just use a
 * query manager to fetch and modify whichever resource is asked for.
 * The club, team or userId that is asked for is what is then being fetch or updated, and any corresponding
 * update to access rights is done with the help of that resource's access table and repository manager?
 * What about the case when an admin in a club wants to update a user in that club?
 * We first check if he is authorized to access that club? Then we check if the user to be modified is
 * a member of that club? Because we don't have a user to user access table? No, too much to manage.
 * Every admin just asks if that resource is part of the club, if it is then he has the rights to modify
 * that resource if he himself is also part of the club.
 * So Club -> isMemberOfClub -> UserId/TeamId -> isPartOfClub
 * If all this is done with an AuthGuard bean, then the resourceId can be safely be passed along to the
 * service after that. And the ids can be used directly with the resourceRepository?
 * Why not just do it the way I'm doing it now? Everything I need is already in the @AuthenticatedPrincipal
 * But it feels bad to filter so much and to pass the actual database entities to the ResourceService.
 * "Does not feel right"
 */

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController {
  private final UserService userService;

  @DeleteMapping("/principal")
  public void deleteSelf(@AuthenticationPrincipal User principal) {
    userService.deleteUser(principal);
  }

  @GetMapping("/club/{clubId}/leaders")
  public Set<BaseUserDTO> getLeaders(@AuthenticationPrincipal User principal, @PathVariable String clubId) {
    // TODO Require Admin
    return principal
        .getClubByClubId(clubId)
        .orElseThrow()
        .getUsers()
        .stream()
        .filter(user -> user.getRolesForClub(clubId).contains(ClubRole.Role.LEADER.name()))
        .map(BaseUserDTO::new)
        .collect(Collectors.toSet());
  }

  @GetMapping("/roles")
  public ClubRole.Role[] getRolesNames() {
    return ClubRole.Role.values();
  }

  @GetMapping("/principal")
  public UserDTO getSelf(@AuthenticationPrincipal User principal) {
    return UserDTO.create(principal);
  }

  @GetMapping("/club/{clubId}/user/{userId}")
  public UserDTO getUser(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String userId) {
    // TODO require role admin
    User user = principal
        .getClubByClubId(clubId)
        .orElseThrow()
        .getUser(userId)
        .orElseThrow();
    return UserDTO.create(user);
  }

  @GetMapping("/club/{clubId}/age-range")
  public Set<BaseUserDTO> getUsersByAgeRange(@AuthenticationPrincipal User principal, @PathVariable String clubId, @RequestParam int minAge,
      @RequestParam int maxAge) {
    // TODO require role admin
    return principal
        .getClubByClubId(clubId)
        .orElseThrow()
        .getUsers()
        .stream()
        .filter(user -> user.getAge() <= maxAge && user.getAge() >= minAge)
        .map(BaseUserDTO::new)
        .collect(Collectors.toSet());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/club/{clubId}/remove/{userId}")
  public void removeUserFromClub(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String userId) {
    // TODO require role admin
    Club club = principal.getClubByClubId(clubId).orElseThrow();
    User user = club.getUser(userId).orElseThrow();
    userService.removeUserFromClub(user, club);
  }

  @PutMapping("/principal")
  public UserDTO updateSelf(@AuthenticationPrincipal User principal, @Valid @RequestBody UpdateUserModel userDetails) {
    return userService.updateUser(principal, userDetails);
  }

  @PutMapping("/club/{clubId}/{userId}")
  public UserDTO updateUser(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String userId,
      @Valid @RequestBody AdminUpdateUserModel userDetails) {
    // TODO Require Admin
    Club club = principal.getClubByClubId(clubId).orElseThrow();
    User user = club.getUser(userId).orElseThrow();
    return userService.updateUser(user, club, userDetails);
  }

  // Principal needs similar functionality to add another parent to his/her children. Needs helper
  // end point to search for a user in the club by email
  @PostMapping("/clubId/{clubId}/children/{userId}")
  public UserDTO updateUserChildren(@AuthenticationPrincipal User principal, @PathVariable String clubId, @PathVariable String userId,
      @RequestBody Set<String> childrenIds) {
    // TODO Require Admin
    Club club = principal.getClubByClubId(clubId).orElseThrow();
    User parent = club.getUser(userId).orElseThrow();
    Set<User> clubChildren = club.getManagedUsers();
    Set<User> validatedChildren = clubChildren
        .stream()
        .filter(child -> childrenIds.contains(child.getUserId()))
        .collect(Collectors.toSet());

    return userService.updateUserChildren(parent, validatedChildren, club);
  }
}
