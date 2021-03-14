package nu.borjessons.clubhouse.impl.service.impl;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.service.ClubService;
import nu.borjessons.clubhouse.impl.service.ClubhouseAbstractService;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClubServiceImpl extends ClubhouseAbstractService implements ClubService {

  private final ClubRepository clubRepository;

  @Override
  public Club saveClub(Club club) {
    return clubRepository.save(club);
  }

  @Override
  public Club getClubByClubId(String clubId) {
    return getOptional(clubRepository.findByClubId(clubId), Club.class, clubId);
  }

  @Override
  public Set<ClubDTO> getAllClubs() {
    return clubRepository.findAll().stream().map(ClubDTO::new).collect(Collectors.toSet());
  }
}
