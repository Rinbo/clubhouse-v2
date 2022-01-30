package nu.borjessons.clubhouse.impl.service;

import java.util.Set;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;

public interface ClubService {
  Club getClubByClubId(String clubId);

  Set<ClubRecord> getAllClubs();

  ClubRecord getPublicClub(String pathname);
}
