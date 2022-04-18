package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;

import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;
import nu.borjessons.clubhouse.impl.dto.AnnouncementRecord;
import nu.borjessons.clubhouse.impl.dto.rest.AnnouncementModel;

public interface AnnouncementService {
  Collection<AnnouncementRecord> getClubAnnouncements(String clubId, Pageable pageable);

  AnnouncementRecord createAnnouncement(String clubId, @Valid AnnouncementModel announcementModel, ImageToken imageToken,
      User principal);

  AnnouncementRecord updateAnnouncement(AnnouncementId announcementId, String clubId, AnnouncementModel announcementModel);

  AnnouncementRecord getAnnouncement(AnnouncementId announcementId);

  void deleteAnnouncement(AnnouncementId announcementId);

  List<AnnouncementRecord> getAllClubAnnouncements(User principal, Pageable pageable);

  int getSize(String clubId);
}
