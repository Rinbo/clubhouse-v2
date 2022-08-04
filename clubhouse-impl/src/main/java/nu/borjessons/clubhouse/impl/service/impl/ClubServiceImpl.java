package nu.borjessons.clubhouse.impl.service.impl;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.dto.rest.ClubColorRecord;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.service.ClubService;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {
  private final ClubRepository clubRepository;

  @Override
  public Set<ClubRecord> getAllClubs() {
    return clubRepository.findAll().stream().map(ClubRecord::new).collect(Collectors.toSet());
  }

  @Override
  public ClubRecord getClubByClubId(String clubId) {
    return new ClubRecord(clubRepository.findByClubId(clubId).orElseThrow());
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
}
