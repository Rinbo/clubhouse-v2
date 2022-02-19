package nu.borjessons.clubhouse.impl.service.impl;

import java.util.Collection;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.AnnouncementRecord;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.service.AnnouncementService;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {
  private final ClubRepository clubRepository;

  // TODO implement pageable instead
  @Override
  public Collection<AnnouncementRecord> getAllAnnouncements(String clubId) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    return club.getAnnouncements().stream().map(AnnouncementRecord::new).toList();
  }
}
