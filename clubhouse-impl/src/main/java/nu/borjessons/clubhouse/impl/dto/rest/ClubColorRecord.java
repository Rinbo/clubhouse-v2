package nu.borjessons.clubhouse.impl.dto.rest;

import nu.borjessons.clubhouse.impl.util.Validate;

public record ClubColorRecord(String primaryColor, String secondaryColor) {
  public ClubColorRecord {
    Validate.validateIsColorOrNull(primaryColor, "primaryColor");
    Validate.validateIsColorOrNull(secondaryColor, "secondaryColor");
  }
}
