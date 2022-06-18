package nu.borjessons.clubhouse.impl.dto.rest;

import nu.borjessons.clubhouse.impl.util.Validate;

public record TeamPostCommentRequest(String comment) {
  public TeamPostCommentRequest {
    Validate.notEmpty(comment, "comment");
  }
}
