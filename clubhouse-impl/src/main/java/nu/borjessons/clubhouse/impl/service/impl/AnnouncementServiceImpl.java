package nu.borjessons.clubhouse.impl.service.impl;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Announcement;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.ImageToken;
import nu.borjessons.clubhouse.impl.data.key.AnnouncementId;
import nu.borjessons.clubhouse.impl.dto.AnnouncementRecord;
import nu.borjessons.clubhouse.impl.dto.rest.AnnouncementModel;
import nu.borjessons.clubhouse.impl.repository.AnnouncementRepository;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.repository.UserRepository;
import nu.borjessons.clubhouse.impl.service.AnnouncementService;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
  private final AnnouncementRepository announcementRepository;
  private final ClubRepository clubRepository;
  private final ClubUserRepository clubUserRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public AnnouncementRecord createAnnouncement(String clubId, AnnouncementModel announcementModel, ImageToken imageToken, long principalId) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, principalId).orElseThrow();

    Announcement announcement = new Announcement();
    announcement.setAuthor(clubUser);
    announcement.setTitle(announcementModel.getTitle());
    announcement.setBody(announcementModel.getBody());
    announcement.setClub(clubUser.getClub());
    announcement.setImageToken(imageToken);

    return new AnnouncementRecord(announcementRepository.save(announcement));
  }

  @Override
  @Transactional
  public void deleteAnnouncement(AnnouncementId announcementId) {
    announcementRepository.deleteAnnouncementByAnnouncementId(announcementId);
  }

  @Override
  @Transactional
  public List<AnnouncementRecord> getAllClubAnnouncements(long principalId, Pageable pageable) {
    List<Long> clubIds = userRepository
        .getReferenceById(principalId)
        .getClubUsers()
        .stream()
        .map(ClubUser::getClub)
        .map(Club::getId)
        .toList();
    return announcementRepository.findAnnouncementsByClubIdIn(clubIds, pageable);
  }

  @Override
  public AnnouncementRecord getAnnouncement(AnnouncementId announcementId) {
    return new AnnouncementRecord(announcementRepository.findAnnouncementByAnnouncementId(announcementId).orElseThrow());
  }

  @Override
  public Collection<AnnouncementRecord> getClubAnnouncements(String clubId, Pageable pageable) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    return announcementRepository.findAnnouncementByClubId(club.getId(), pageable);
  }

  @Override
  public int getSize(String clubId) {
    return announcementRepository.getSize(clubId);
  }

  @Override
  @Transactional
  public AnnouncementRecord updateAnnouncement(AnnouncementId announcementId, String clubId, AnnouncementModel announcementModel) {
    Announcement announcement = announcementRepository.findAnnouncementByAnnouncementId(announcementId).orElseThrow();
    announcement.setTitle(announcementModel.getTitle());
    announcement.setBody(announcementModel.getBody());
    return new AnnouncementRecord(announcementRepository.save(announcement));
  }
}
