package nu.borjessons.clubhouse.impl.service;

import java.util.Set;

import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.dto.rest.ClubColorRecord;

public interface ClubService {
  void deleteClub(String clubId);

  Set<ClubRecord> getAllClubs();

  ClubRecord getClubByClubId(String clubId);

  ClubRecord getPublicClub(String pathname);

  ClubRecord updateColor(String clubId, ClubColorRecord clubColorRecord);
}
