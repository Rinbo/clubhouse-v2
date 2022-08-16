package nu.borjessons.clubhouse.impl.controller.club;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.dto.ClubStatisticsRecord;
import nu.borjessons.clubhouse.impl.dto.rest.ClubColorRecord;
import nu.borjessons.clubhouse.impl.service.ClubService;

@RequestMapping("/clubs/{clubId}")
@RequiredArgsConstructor
@RestController
public class ClubController {
  private final ClubService clubService;

  @PreAuthorize("hasRole('OWNER')")
  @DeleteMapping
  public ResponseEntity<String> deleteClub(@PathVariable String clubId) {
    try {
      clubService.deleteClub(clubId);
    } catch (IOException e) {
      return ResponseEntity.internalServerError().body("Unable to delete club");
    }
    return ResponseEntity.ok("Club deleted");
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping
  public ClubRecord getClub(@PathVariable String clubId) {
    return clubService.getClubByClubId(clubId);
  }

  @PreAuthorize("hasRole('USER')")
  @GetMapping(path = "/statistics")
  public ClubStatisticsRecord getClubStatistics(@PathVariable String clubId) {
    return clubService.getClubStatistics(clubId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping(path = "/color")
  public ClubRecord updateColor(@PathVariable String clubId, @RequestBody ClubColorRecord clubColorRecord) {
    return clubService.updateColor(clubId, clubColorRecord);
  }
}
