package nu.borjessons.clubhouse.impl.controller.open;

import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.service.ClubService;

@RequestMapping("/public")
@RequiredArgsConstructor
@RestController
public class OpenClubController {
  private final ClubService clubService;

  @GetMapping(path = "/clubs")
  public Set<ClubRecord> getAllClubs() {
    return clubService.getAllClubs();
  }

  @GetMapping(path = "/clubs/{pathname}")
  public ClubRecord getPublicClub(@PathVariable String pathname) {
    return clubService.getPublicClub(pathname);
  }
}
