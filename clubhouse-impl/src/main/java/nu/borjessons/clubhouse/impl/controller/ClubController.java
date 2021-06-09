package nu.borjessons.clubhouse.impl.controller;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.service.ClubService;

@RequestMapping("/clubs")
@RequiredArgsConstructor
@RestController
public class ClubController {
  private final ClubService clubService;

  @GetMapping(path = "/public")
  public Set<ClubDTO> getAllClubs() {
    return clubService.getAllClubs();
  }

  @GetMapping(path = "/{clubId}")
  public ClubDTO getClub(@PathVariable String clubId) {
    // TODO Require role user in club and move to userController
    final Club club = clubService.getClubByClubId(clubId);
    return new ClubDTO(club);
  }

  @GetMapping(path = "/public/{pathname}")
  public ClubDTO getPublicClub(@PathVariable String pathname) {
    return clubService.getPublicClub(pathname);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping(path = "/{clubId}/users")
  public Set<UserDTO> getUsers(@PathVariable String clubId) {
    // TODO Require role user in club and move to userController
    return clubService
        .getClubByClubId(clubId)
        .getUsers()
        .stream()
        .map(UserDTO::create)
        .collect(Collectors.toSet());
  }
}
