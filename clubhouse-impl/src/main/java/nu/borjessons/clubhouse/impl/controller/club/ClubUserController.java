package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.BaseUserDTO;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.service.ClubService;
import nu.borjessons.clubhouse.impl.service.ClubUserService;

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
 * <p>
 * Maybe one can have an authChecker that instead just verifies the principal against club access. If asked for resource is part of club
 * Then it is valid to pass it on. That way I don't have to make these filters and whatnot in the controller. I can simply provide the
 * principal (user) id to the authGuard, and then it looks up if the requested resource is present in that club
 */
@RequiredArgsConstructor
@RestController
public class ClubUserController {
  private final ClubService clubService;
  private final ClubUserService clubUserService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/clubs/{clubId}/user/{userId}")
  public UserDTO getUser(@PathVariable String clubId, @PathVariable String userId) {
    User user = clubService
        .getClubByClubId(clubId)
        .getUser(userId)
        .orElseThrow();
    return UserDTO.create(user);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/clubs/{clubId}/user/age-range")
  public Collection<BaseUserDTO> getUsersByAgeRange(@PathVariable String clubId, @RequestParam int minAge, @RequestParam int maxAge) {
    return clubService
        .getClubByClubId(clubId)
        .getUsers()
        .stream()
        .filter(user -> user.getAge() >= minAge && user.getAge() <= maxAge)
        .map(BaseUserDTO::new)
        .collect(Collectors.toList());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/clubs/{clubId}/remove/{userId}")
  public void removeUserFromClub(@PathVariable String clubId, @PathVariable String userId) {
    clubUserService.removeUserFromClub(userId, clubId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/clubs/{clubId}/{userId}")
  public UserDTO updateUser(@PathVariable String clubId, @PathVariable String userId, @Valid @RequestBody AdminUpdateUserModel userDetails) {
    User user = clubUserService.updateUser(userId, clubId, userDetails);
    return UserDTO.create(user);
  }

  /**
   * Principal needs similar functionality to add another parent to his/her children. Needs helper
   * end point to search for a user in the club by email
   * Update 2021-06-20: Consider having a remove and add method since DELETE PUT is not as applicable to a resource that
   * can belong to several entities
   */
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/clubId/{clubId}/children/{userId}")
  public UserDTO addExistingChildrenToUser(@PathVariable String clubId, @PathVariable String userId, @RequestBody Set<String> childrenIds) {
    User user = clubUserService.addExistingChildrenToUser(userId, clubId, childrenIds);
    return UserDTO.create(user);
  }
}
