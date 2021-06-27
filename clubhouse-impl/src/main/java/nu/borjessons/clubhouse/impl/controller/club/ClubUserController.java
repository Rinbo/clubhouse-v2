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

@RequiredArgsConstructor
@RestController
public class ClubUserController {
  private final ClubService clubService;
  private final ClubUserService clubUserService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/clubs/{clubId}/user/{userId}")
  public UserDTO getUser(@PathVariable String clubId, @PathVariable String userId) {
    return clubUserService.getClubUser(clubId, userId);
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
