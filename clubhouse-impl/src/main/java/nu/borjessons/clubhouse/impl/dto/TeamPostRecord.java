package nu.borjessons.clubhouse.impl.dto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;

import nu.borjessons.clubhouse.impl.data.BaseEntity;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.TeamPost;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;
import nu.borjessons.clubhouse.impl.util.Validate;

public record TeamPostRecord(
    TeamPostId teamPostId,
    String title,
    String body,
    boolean sticky,
    String teamId,
    Collection<TeamPostCommentRecord> comments,
    BaseUserRecord author,
    LocalDateTime createdAt) {
  private static BaseUserRecord createBaseUserRecord(ClubUser clubUser) {
    if (clubUser == null) return null;
    return new BaseUserRecord(clubUser.getUser());
  }

  public TeamPostRecord {
    Validate.notNull(teamPostId, "teamPostId");
    Validate.notNull(title, "title");
    Validate.notNull(body, "body");
    Validate.notNull(teamId, "teamId");
    Validate.notNull(comments, "comments");
    Validate.notNull(createdAt, "createdAt");
  }

  public TeamPostRecord(TeamPost teamPost) {
    this(teamPost.getTeamPostId(),
        teamPost.getTitle(),
        teamPost.getBody(),
        teamPost.isSticky(),
        teamPost.getTeam().getTeamId(),
        teamPost.getTeamPostComments().stream().sorted(Comparator.comparing(BaseEntity::getCreatedAt)).map(TeamPostCommentRecord::new).toList(),
        createBaseUserRecord(teamPost.getClubUser()),
        teamPost.getCreatedAt());
  }
}
