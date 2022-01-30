package nu.borjessons.clubhouse.impl.controller.club;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.service.ClubService;

@RequestMapping("/clubs")
@RequiredArgsConstructor
@RestController
public class ClubController {
  private final ClubService clubService;

  @PreAuthorize("hasRole('USER')")
  @GetMapping(path = "/{clubId}")
  public ClubRecord getClub(@PathVariable String clubId) {
    return new ClubRecord(clubService.getClubByClubId(clubId));
  }
}
