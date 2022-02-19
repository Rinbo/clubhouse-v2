package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.AnnouncementRecord;
import nu.borjessons.clubhouse.impl.service.AnnouncementService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clubs/{clubId}/announcement")
public class AnnouncementController {
  private final AnnouncementService announcementService;

  @GetMapping
  public Collection<AnnouncementRecord> getAnnouncements(@PathVariable String clubId) {

    return null;
  }

}
