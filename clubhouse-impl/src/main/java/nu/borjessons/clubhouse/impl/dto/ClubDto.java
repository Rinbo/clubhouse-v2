package nu.borjessons.clubhouse.impl.dto;

import nu.borjessons.clubhouse.impl.data.Club;

public record ClubDto(String clubId, String name, String path, Club.Type type, String imageTokenId) {
  public ClubDto(Club club) {
    this(club.getClubId(), club.getName(), club.getPath(), club.getType(), club.getLogoId() != null ? club.getLogoId().toString() : null);
  }
}
