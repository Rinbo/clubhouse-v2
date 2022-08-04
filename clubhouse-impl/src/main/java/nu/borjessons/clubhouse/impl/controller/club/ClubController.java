package nu.borjessons.clubhouse.impl.controller.club;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.dto.rest.ClubColorRecord;
import nu.borjessons.clubhouse.impl.service.ClubService;

@RequestMapping("/clubs")
@RequiredArgsConstructor
@RestController
public class ClubController {
  private final ClubService clubService;

  @PreAuthorize("hasRole('USER')")
  @GetMapping(path = "/{clubId}")
  public ClubRecord getClub(@PathVariable String clubId) {
    return clubService.getClubByClubId(clubId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping(path = "/{clubId}/color")
  public ClubRecord updateColor(@PathVariable String clubId, @RequestBody ClubColorRecord clubColorRecord) {
    return clubService.updateColor(clubId, clubColorRecord);
  }
}
