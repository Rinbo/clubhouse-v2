package nu.borjessons.clubhouse.impl.service;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;

import java.util.Set;

public interface ClubService {

  Club saveClub(Club club);

  Club getClubByClubId(String clubId);

  Set<ClubDTO> getAllClubs();

  ClubDTO getPublicClub(String pathname);
}
