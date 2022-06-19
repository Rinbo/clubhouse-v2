package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;

import org.springframework.data.domain.PageRequest;

import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;
import nu.borjessons.clubhouse.impl.dto.TeamPostCommentRecord;
import nu.borjessons.clubhouse.impl.dto.TeamPostRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostCommentRequest;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostRequest;

public interface TeamPostService {
  TeamPostRecord createPost(User user, String clubId, String teamId, TeamPostRequest teamPostRequest);

  Collection<TeamPostRecord> getPosts(String teamId, PageRequest pageRequest);

  TeamPostRecord toggleSticky(TeamPostId teamPostId);

  TeamPostRecord updatePost(User principal, String clubId, String teamId, TeamPostId teamPostId, TeamPostRequest teamPostRequest);

  TeamPostRecord getPost(TeamPostId teamPostId);

  void deletePost(TeamPostId teamPostId);

  void deletePost(User principal, String clubId, TeamPostId teamPostId);

  TeamPostRecord createComment(User principal, String clubId, TeamPostId teamPostId, TeamPostCommentRequest teamPostCommentRequest);

  TeamPostRecord updateComment(User principal, String clubId, long teamPostCommentId, TeamPostCommentRequest teamPostCommentRequest);

  void deleteTeamPostComment(long teamPostCommentId);

  void deleteTeamPostComment(User principal, String clubId, long teamPostCommentId);

  Collection<TeamPostCommentRecord> getTeamPostComments(TeamPostId teamPostId, PageRequest pageRequest);
}
