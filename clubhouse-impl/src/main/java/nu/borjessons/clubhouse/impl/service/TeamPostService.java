package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;

import org.springframework.data.domain.PageRequest;

import nu.borjessons.clubhouse.impl.data.TeamPost;
import nu.borjessons.clubhouse.impl.data.TeamPostComment;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;
import nu.borjessons.clubhouse.impl.dto.TeamPostCommentRecord;
import nu.borjessons.clubhouse.impl.dto.TeamPostRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostCommentRequest;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostRequest;

public interface TeamPostService {
  TeamPostRecord createComment(long principalId, String clubId, TeamPostId teamPostId, TeamPostCommentRequest teamPostCommentRequest);

  TeamPostRecord createPost(long principalId, String clubId, String teamId, TeamPostRequest teamPostRequest);

  void deletePost(TeamPost teamPost);

  void deleteTeamPostComment(TeamPostComment teamPostComment);

  int getCommentSize(TeamPostId teamPostId);

  TeamPostRecord getPost(TeamPostId teamPostId);

  Collection<TeamPostRecord> getPosts(String teamId, PageRequest pageRequest);

  int getSize(String teamId);

  Collection<TeamPostCommentRecord> getTeamPostComments(TeamPostId teamPostId, PageRequest pageRequest);

  TeamPostRecord toggleSticky(TeamPostId teamPostId);

  TeamPostRecord updateComment(TeamPostComment teamPostComment, TeamPostCommentRequest teamPostCommentRequest);

  TeamPostRecord updatePost(TeamPost teamPost, TeamPostRequest teamPostRequest);
}
