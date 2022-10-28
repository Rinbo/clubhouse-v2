package nu.borjessons.clubhouse.impl.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
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
import nu.borjessons.clubhouse.impl.dto.AnnouncementRecord;
import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;
import nu.borjessons.clubhouse.impl.security.util.SecurityUtil;
import nu.borjessons.clubhouse.impl.service.AnnouncementService;
import nu.borjessons.clubhouse.impl.service.ClubUserService;
import nu.borjessons.clubhouse.impl.service.UserService;

@RequestMapping("/principal")
@RequiredArgsConstructor
@RestController
public class PrincipalController {
  private static void removeJwtTokenCookie(HttpServletResponse httpServletResponse) {
    httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, SecurityUtil.getLogoutCookie().toString());
  }

  private final AnnouncementService announcementService;
  private final ClubUserService clubUserService;
  private final UserService userService;

  @PutMapping("/add-parent")
  public void addParentToChildren(@AuthenticationPrincipal User principal, @RequestParam UserId parentId, @RequestParam UserId childId) {
    userService.addParentToChild(principal.getUserId(), childId, parentId);
  }

  @DeleteMapping()
  public void deleteSelf(@AuthenticationPrincipal User principal, HttpServletResponse httpServletResponse) {
    userService.deleteUser(principal.getId());
    removeJwtTokenCookie(httpServletResponse);
  }

  @GetMapping("/clubs/all-club-users")
  public Collection<ClubUserDto> getAllMyClubUsers(@AuthenticationPrincipal User principal) {
    return clubUserService.getAllUsersClubUsers(principal.getUserId());
  }

  @GetMapping("/announcements")
  public Collection<AnnouncementRecord> getAnnouncements(@AuthenticationPrincipal User principal, @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return announcementService.getAllClubAnnouncements(principal, PageRequest.of(page, size, Sort.by("createdAt").descending()));
  }

  @GetMapping("/children")
  public Collection<BaseUserRecord> getChildren(@AuthenticationPrincipal User principal) {
    return userService.getChildren(principal.getId());
  }

  @GetMapping("/clubs")
  public Collection<ClubRecord> getMyClubs(@AuthenticationPrincipal User principal) {
    return userService.getMyClubs(principal.getUserId());
  }

  @GetMapping()
  public UserDto getSelf(@AuthenticationPrincipal User principal) {
    return userService.getById(principal.getId());
  }

  @PutMapping("/child/{childId}")
  public UserDto updateChild(@AuthenticationPrincipal User principal, @PathVariable UserId childId, @Valid @RequestBody UpdateUserModel userDetails) {
    return userService.updateChild(childId, principal.getUserId(), userDetails);
  }

  @PutMapping()
  public UserDto updateSelf(@AuthenticationPrincipal User principal, @Valid @RequestBody UpdateUserModel userDetails) {
    return userService.updateUser(principal.getId(), userDetails);
  }
}
