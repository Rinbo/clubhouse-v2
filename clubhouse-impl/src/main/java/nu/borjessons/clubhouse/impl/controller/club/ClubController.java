package nu.borjessons.clubhouse.impl.controller.club;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.ClubDto;
import nu.borjessons.clubhouse.impl.service.ClubService;

@RequestMapping("/clubs")
@RequiredArgsConstructor
@RestController
public class ClubController {
  private final ClubService clubService;

  @PreAuthorize("hasRole('USER')")
  @GetMapping(path = "/{clubId}")
  public ClubDto getClub(@PathVariable String clubId) {
    return new ClubDto(clubService.getClubByClubId(clubId));
  }
}
