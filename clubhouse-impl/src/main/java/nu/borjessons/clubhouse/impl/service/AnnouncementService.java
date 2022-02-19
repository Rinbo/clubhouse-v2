package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;

import nu.borjessons.clubhouse.impl.dto.AnnouncementRecord;

public interface AnnouncementService {
  Collection<AnnouncementRecord> getAllAnnouncements(String clubId);
}
