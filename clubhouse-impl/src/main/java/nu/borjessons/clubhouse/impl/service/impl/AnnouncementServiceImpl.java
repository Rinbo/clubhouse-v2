package nu.borjessons.clubhouse.impl.service.impl;

import java.util.Collection;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Announcement;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.AnnouncementRecord;
import nu.borjessons.clubhouse.impl.repository.AnnouncementRepository;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.service.AnnouncementService;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
  private final ClubRepository clubRepository;
  private final AnnouncementRepository announcementRepository;
  private final ClubUserRepository clubUserRepository;

  // TODO implement pageable instead
  @Override
  public Collection<AnnouncementRecord> getAllAnnouncements(String clubId) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    return club.getAnnouncements().stream().map(AnnouncementRecord::new).toList();
  }

  @Override
  public AnnouncementRecord createAnnouncement(String clubId, AnnouncementRecord announcementRecord, User principal) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, principal.getId()).orElseThrow();

    Announcement announcement = new Announcement();
    announcement.setAuthor(clubUser);
    announcement.setTitle(announcementRecord.title());
    announcement.setBody(announcementRecord.body());
    announcement.setClub(clubUser.getClub());

    return new AnnouncementRecord(announcementRepository.save(announcement));
  }
}
