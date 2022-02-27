package nu.borjessons.clubhouse.impl.controller.club;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;
import nu.borjessons.clubhouse.impl.dto.AnnouncementRecord;
import nu.borjessons.clubhouse.impl.dto.rest.AnnouncementModel;
import nu.borjessons.clubhouse.impl.service.AnnouncementService;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@RequestMapping("/clubs/{clubId}/announcements")
public class AnnouncementController {
  private final AnnouncementService announcementService;

  @GetMapping
  public Collection<AnnouncementRecord> getAnnouncements(@PathVariable String clubId, @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return announcementService.getAnnouncements(clubId, PageRequest.of(page, size, Sort.by("createdAt").descending()));
  }

  @GetMapping("/{announcementId}")
  public AnnouncementRecord getAnnouncement(@PathVariable AnnouncementId announcementId, @PathVariable String clubId) {
    return announcementService.getAnnouncement(announcementId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public AnnouncementRecord createAnnouncement(@AuthenticationPrincipal User principal, @PathVariable String clubId,
      @Valid @RequestBody AnnouncementModel announcementModel) {
    return announcementService.createAnnouncement(clubId, announcementModel, principal);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{announcementId}")
  public AnnouncementRecord createAnnouncement(@PathVariable String clubId, @PathVariable AnnouncementId announcementId,
      @Valid @RequestBody AnnouncementModel announcementModel) {
    return announcementService.updateAnnouncement(announcementId, clubId, announcementModel);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{announcementId}")
  public void deleteAnnouncement(@PathVariable AnnouncementId announcementId, @PathVariable String clubId) {
    announcementService.deleteAnnouncement(announcementId);
  }
}


