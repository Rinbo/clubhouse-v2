package nu.borjessons.clubhouse.impl.dto;

import nu.borjessons.clubhouse.impl.data.Club;

public record ClubDTO(String clubId, String name, String path, Club.Type type) {
  public ClubDTO(Club club) {
    this(club.getClubId(), club.getName(), club.getPath(), club.getType());
  }
}
