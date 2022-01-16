package nu.borjessons.clubhouse.impl.dto;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;

public record ClubDto(String clubId, String name, String path, Club.Type type, ImageTokenId imageTokenId) {
  public ClubDto(Club club) {
    this(club.getClubId(), club.getName(), club.getPath(), club.getType(), club.getLogoId());
  }
}
