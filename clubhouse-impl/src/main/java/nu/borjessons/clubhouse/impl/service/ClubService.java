package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;
import java.util.Set;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;

public interface ClubService {
  Collection<User> getLeaders(String clubId);

  void saveClub(Club club);

  Club getClubByClubId(String clubId);

  Set<ClubDTO> getAllClubs();

  ClubDTO getPublicClub(String pathname);
}
