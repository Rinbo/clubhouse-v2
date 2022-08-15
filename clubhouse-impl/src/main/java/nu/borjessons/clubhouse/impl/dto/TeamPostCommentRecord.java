package nu.borjessons.clubhouse.impl.dto;

import java.time.LocalDateTime;

import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.TeamPostComment;
import nu.borjessons.clubhouse.impl.util.Validate;

public record TeamPostCommentRecord(long id, String comment, BaseUserRecord author, LocalDateTime createdAt) {
  private static BaseUserRecord createBaseUserRecord(ClubUser clubUser) {
    if (clubUser == null) return null;
    return new BaseUserRecord(clubUser.getUser());
  }

  public TeamPostCommentRecord {
    Validate.isPositive(id, "id");
    Validate.notNull(comment, "comment");
    Validate.notNull(createdAt, "createdAt");
  }

  public TeamPostCommentRecord(TeamPostComment teamPostComment) {
    this(teamPostComment.getId(),
        teamPostComment.getComment(),
        createBaseUserRecord(teamPostComment.getClubUser()),
        teamPostComment.getCreatedAt());
  }
}
