package nu.borjessons.clubhouse.impl.controller;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.service.ClubService;
import org.springframework.security.access.prepost.PreAuthorize;
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

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/{clubId}/roles")
  public Set<String> getActiveClub(@PathVariable String clubId) {
    return getPrincipal().getRolesForClub(clubId);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/principal/active")
  public ClubDTO getActiveClub() {
    return new ClubDTO(getPrincipal().getActiveClub());
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/principal")
  public Set<ClubDTO> getClubs() {
    return getPrincipal()
        .getClubs()
        .stream()
        .map(ClubDTO::new)
        .collect(Collectors.toSet());
  }
}
