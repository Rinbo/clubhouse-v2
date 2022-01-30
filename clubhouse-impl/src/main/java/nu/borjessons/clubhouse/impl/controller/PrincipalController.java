package nu.borjessons.clubhouse.impl.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;
import nu.borjessons.clubhouse.impl.service.ClubUserService;
import nu.borjessons.clubhouse.impl.service.UserService;

@RequestMapping("/principal")
@RequiredArgsConstructor
@RestController
public class PrincipalController {
  private final UserService userService;
  private final ClubUserService clubUserService;

  @DeleteMapping()
  public void deleteSelf(@AuthenticationPrincipal User principal) {
    userService.deleteUser(principal.getId());
  }

  @GetMapping()
  public UserDto getSelf(@AuthenticationPrincipal User principal) {
    return userService.getById(principal.getId());
  }

  @PutMapping()
  public UserDto updateSelf(@AuthenticationPrincipal User principal, @Valid @RequestBody UpdateUserModel userDetails) {
    return userService.updateUser(principal.getId(), userDetails);
  }

  @PutMapping("/child/{childId}")
  public UserDto updateChild(@AuthenticationPrincipal User principal, @PathVariable UserId childId, @Valid @RequestBody UpdateUserModel userDetails) {
    return userService.updateChild(childId, principal.getUserId(), userDetails);
  }

  @GetMapping("/clubs")
  public List<ClubRecord> getMyClubs(@AuthenticationPrincipal User principal) {
    return userService.getMyClubs(principal.getUserId());
  }

  @GetMapping("/clubs/all-club-users")
  public List<ClubUserDto> getAllMyClubUsers(@AuthenticationPrincipal User principal) {
    return clubUserService.getAllUsersClubUsers(principal.getUserId());
  }

  @PutMapping("/add-parent")
  public void addParentToChildren(@AuthenticationPrincipal User principal, @RequestParam UserId parentId, @RequestParam UserId childId) {
    userService.addParentToChild(principal.getUserId(), childId, parentId);
  }
}
