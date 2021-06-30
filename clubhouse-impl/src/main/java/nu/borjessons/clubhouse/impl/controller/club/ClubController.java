package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;
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
  public Collection<ClubUserDTO> getLeaders(@PathVariable String clubId) {
    return clubService.getLeaders(clubId);
  }

  //TODO return ClubUserDTO instead - Shouldn't service return List of DTOs?
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping(path = "/{clubId}/users")
  public Collection<ClubUserDTO> getUsers(@PathVariable String clubId) {
    return clubService
        .getClubByClubId(clubId)
        .getClubUsers()
        .stream()
        .map(ClubUserDTO::new)
        .collect(Collectors.toList());
  }
}
