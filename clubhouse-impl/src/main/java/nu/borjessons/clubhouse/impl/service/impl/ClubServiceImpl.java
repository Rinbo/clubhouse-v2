package nu.borjessons.clubhouse.impl.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
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
  public Set<ClubDTO> getAllClubs() {
    return clubRepository.findAll().stream().map(ClubDTO::new).collect(Collectors.toSet());
  }

  @Override
  public ClubDTO getPublicClub(String pathname) {
    return clubRepository.findByPath(pathname).map(ClubDTO::new).orElseThrow();
  }
}
