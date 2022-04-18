package nu.borjessons.clubhouse.impl.dto;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.util.Validate;

public record ClubRecord(String clubId, String name, String path, Club.Type type, String imageTokenId) {
  public ClubRecord {
    Validate.notEmpty(clubId, "clubId");
    Validate.notEmpty(name, "name");
    Validate.notEmpty(path, "path");
    Validate.notNull(type, "type");
  }

  public ClubRecord(Club club) {
    this(club.getClubId(), club.getName(), club.getPath(), club.getType(), club.getLogo() != null ? club.getLogo().getImageTokenId().toString() : null);
  }
}
