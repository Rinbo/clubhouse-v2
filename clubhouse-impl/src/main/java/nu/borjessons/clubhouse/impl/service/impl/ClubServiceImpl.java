package nu.borjessons.clubhouse.impl.service.impl;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.dto.ClubStatisticsRecord;
import nu.borjessons.clubhouse.impl.dto.rest.ClubColorRecord;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.service.ClubService;
import nu.borjessons.clubhouse.impl.service.ImageService;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {
  private final ClubRepository clubRepository;

  @PersistenceContext
  private final EntityManager entityManager;
  private final ImageService imageService;

  // TODO remove all images in club including top folder (recursive delete)
  @Transactional
  @Override
  public void deleteClub(String clubId) {
    clubRepository.deleteByClubId(clubId);
  }

  @Override
  public Set<ClubRecord> getAllClubs() {
    return clubRepository.findAll().stream().map(ClubRecord::new).collect(Collectors.toSet());
  }

  @Override
  public ClubRecord getClubByClubId(String clubId) {
    return new ClubRecord(clubRepository.findByClubId(clubId).orElseThrow());
  }

  @Override
  public ClubStatisticsRecord getClubStatistics(String clubId) {
    int userCount = getClubUserCount(clubId).intValue();
    int teamCount = getTeamCount(clubId).intValue();
    int announcementCount = getAnnouncementCount(clubId).intValue();
    int imageCount = imageService.getClubImagePaths(clubId).size();

    return new ClubStatisticsRecord(userCount, teamCount, announcementCount, imageCount);
  }

  @Override
  public ClubRecord getPublicClub(String pathname) {
    return clubRepository.findByPath(pathname).map(ClubRecord::new).orElseThrow();
  }

  @Override
  public ClubRecord updateColor(String clubId, ClubColorRecord clubColorRecord) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow(() -> new NoSuchElementException("Could not find club with id:" + clubId));
    club.setPrimaryColor(clubColorRecord.primaryColor());
    club.setSecondaryColor(clubColorRecord.secondaryColor());

    return new ClubRecord(clubRepository.save(club));
  }

  private Long getAnnouncementCount(String clubId) {
    return entityManager.createQuery("SELECT COUNT(a) FROM Announcement a WHERE a.club.clubId=?1", Long.class)
        .setParameter(1, clubId)
        .getSingleResult();
  }

  private Long getClubUserCount(String clubId) {
    return entityManager.createQuery("SELECT COUNT(cu) FROM ClubUser cu WHERE cu.club.clubId=?1", Long.class)
        .setParameter(1, clubId)
        .getSingleResult();
  }

  private Long getTeamCount(String clubId) {
    return entityManager.createQuery("SELECT COUNT(t) FROM Team t WHERE t.club.clubId=?1", Long.class)
        .setParameter(1, clubId)
        .getSingleResult();
  }
}
