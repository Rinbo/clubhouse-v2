package nu.borjessons.clubhouse.impl.service;

import java.util.Set;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubDto;

public interface ClubService {
  Club getClubByClubId(String clubId);

  Set<ClubDto> getAllClubs();

  ClubDto getPublicClub(String pathname);
}
