package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;

import org.springframework.data.domain.PageRequest;

import nu.borjessons.clubhouse.impl.data.TeamPost;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;
import nu.borjessons.clubhouse.impl.dto.TeamPostCommentRecord;
import nu.borjessons.clubhouse.impl.dto.TeamPostRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostCommentRequest;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostRequest;

public interface TeamPostService {
  TeamPostRecord createComment(User principal, String clubId, TeamPostId teamPostId, TeamPostCommentRequest teamPostCommentRequest);

  TeamPostRecord createPost(User user, String clubId, String teamId, TeamPostRequest teamPostRequest);

  void deletePost(TeamPost teamPost);

  void deleteTeamPostComment(long teamPostCommentId);

  void deleteTeamPostComment(User principal, String clubId, long teamPostCommentId);

  int getCommentSize(TeamPostId teamPostId);

  TeamPostRecord getPost(TeamPostId teamPostId);

  Collection<TeamPostRecord> getPosts(String teamId, PageRequest pageRequest);

  int getSize(String teamId);

  Collection<TeamPostCommentRecord> getTeamPostComments(TeamPostId teamPostId, PageRequest pageRequest);

  TeamPostRecord toggleSticky(TeamPostId teamPostId);

  TeamPostRecord updateComment(User principal, String clubId, long teamPostCommentId, TeamPostCommentRequest teamPostCommentRequest);

  TeamPostRecord updatePost(TeamPost teamPost, TeamPostRequest teamPostRequest);
}
