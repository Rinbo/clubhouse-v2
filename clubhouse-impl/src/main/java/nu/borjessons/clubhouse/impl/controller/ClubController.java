package nu.borjessons.clubhouse.impl.controller;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.service.ClubService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/clubs")
@RequiredArgsConstructor
@RestController
public class ClubController extends AbstractController {

  private final ClubService clubService;

  /**
   * Public endpoints
   */

  @GetMapping(path = "/public/{pathname}")
  public ClubDTO getPublicClub(@PathVariable String pathname) {
    return clubService.getPublicClub(pathname);
  }

  @GetMapping(path = "/public")
  public Set<ClubDTO> getAllClubs() {
    return clubService.getAllClubs();
  }

  /**
   * Private endpoints
   */

  @GetMapping(path = "/{clubId}/users")
  public Set<UserDTO> getUsers(@PathVariable String clubId) {
    // TODO Require role user in club
    return clubService
        .getClubByClubId(clubId)
        .getUsers()
        .stream()
        .map(UserDTO::create)
        .collect(Collectors.toSet());
  }
}
