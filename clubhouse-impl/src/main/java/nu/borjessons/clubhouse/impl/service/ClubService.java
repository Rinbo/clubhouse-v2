package nu.borjessons.clubhouse.impl.service;

import java.util.Set;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;

public interface ClubService {
  Club getClubByClubId(String clubId);

  Set<ClubDTO> getAllClubs();

  ClubDTO getPublicClub(String pathname);
}
