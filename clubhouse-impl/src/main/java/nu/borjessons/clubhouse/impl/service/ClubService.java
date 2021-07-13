package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;
import java.util.Set;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;

public interface ClubService {
  Collection<ClubUserDTO> getLeaders(String clubId);

  Club getClubByClubId(String clubId);

  Set<ClubDTO> getAllClubs();

  ClubDTO getPublicClub(String pathname);
}
