package nu.borjessons.clubhouse.impl.dto.rest;

import nu.borjessons.clubhouse.impl.util.Validate;

public record TeamPostRequest(String title, String body) {
  public TeamPostRequest {
    Validate.notNull(title, "title");
    Validate.notNull(body, "body");
  }
}
