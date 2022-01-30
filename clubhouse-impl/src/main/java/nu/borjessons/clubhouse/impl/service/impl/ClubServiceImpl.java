package nu.borjessons.clubhouse.impl.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.service.ClubService;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl implements ClubService {
  private final ClubRepository clubRepository;

  @Override
  public Club getClubByClubId(String clubId) {
    return clubRepository.findByClubId(clubId).orElseThrow();
  }

  @Override
  public Set<ClubRecord> getAllClubs() {
    return clubRepository.findAll().stream().map(ClubRecord::new).collect(Collectors.toSet());
  }

  @Override
  public ClubRecord getPublicClub(String pathname) {
    return clubRepository.findByPath(pathname).map(ClubRecord::new).orElseThrow();
  }
}
