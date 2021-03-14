package nu.borjessons.clubhouse.impl.controller;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.service.ClubService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;

@RequestMapping("/clubs")
@RequiredArgsConstructor
@RestController
public class ClubController extends AbstractController {

  private final ClubService clubService;

  @GetMapping()
  public Set<ClubDTO> getAllClubs() {
    Set<ClubDTO> clubs = clubService.getAllClubs();
    return clubs;
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/principal/active")
  public ClubDTO getActiveClub() {
    return new ClubDTO(getPrincipal().getActiveClub());
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/principal")
  public Set<ClubDTO> getClubs() {
    Set<Club> clubs = getPrincipal().getClubs();
    return clubs.stream().map(ClubDTO::new).collect(Collectors.toSet());
  }
}
