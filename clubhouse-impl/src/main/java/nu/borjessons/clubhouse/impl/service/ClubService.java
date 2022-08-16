package nu.borjessons.clubhouse.impl.service;

import java.io.IOException;
import java.util.Set;

import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.dto.ClubStatisticsRecord;
import nu.borjessons.clubhouse.impl.dto.rest.ClubColorRecord;

public interface ClubService {
  void deleteClub(String clubId) throws IOException;

  Set<ClubRecord> getAllClubs();

  ClubRecord getClubByClubId(String clubId);

  ClubStatisticsRecord getClubStatistics(String clubId);

  ClubRecord getPublicClub(String pathname);

  ClubRecord updateColor(String clubId, ClubColorRecord clubColorRecord);
}
