package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.ClubRole;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.BaseUserDTO;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.service.ClubService;

@RequestMapping("/clubs")
@RequiredArgsConstructor
@RestController
public class ClubController {
  private final ClubService clubService;

  @PreAuthorize("hasRole('USER')")
  @GetMapping(path = "/{clubId}")
  public ClubDTO getClub(@PathVariable String clubId) {
    return new ClubDTO(clubService.getClubByClubId(clubId));
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{clubId}/leaders")
  public Set<BaseUserDTO> getLeaders(@AuthenticationPrincipal User principal, @PathVariable String clubId) {
    return principal
        .getClubByClubId(clubId)
        .orElseThrow()
        .getUsers()
        .stream()
        .filter(user -> user.getRolesForClub(clubId).contains(ClubRole.Role.LEADER.name()))
        .map(BaseUserDTO::new)
        .collect(Collectors.toSet());
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping(path = "/{clubId}/users")
  public Set<UserDTO> getUsers(@PathVariable String clubId) {
    return clubService
        .getClubByClubId(clubId)
        .getUsers()
        .stream()
        .map(UserDTO::create)
        .collect(Collectors.toSet());
  }
}
