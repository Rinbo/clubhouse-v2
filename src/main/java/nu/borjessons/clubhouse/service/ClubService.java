package nu.borjessons.clubhouse.service;

import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.dto.ClubDTO;

import java.util.Set;

public interface ClubService {

    Club getClubByClubId(String clubId);

    Set<ClubDTO> getAllClubs();
}
