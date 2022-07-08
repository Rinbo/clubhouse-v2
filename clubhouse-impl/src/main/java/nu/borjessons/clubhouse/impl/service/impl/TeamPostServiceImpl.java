package nu.borjessons.clubhouse.impl.service.impl;

import java.util.Collection;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TeamPost;
import nu.borjessons.clubhouse.impl.data.TeamPostComment;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.TeamPostId;
import nu.borjessons.clubhouse.impl.dto.TeamPostCommentRecord;
import nu.borjessons.clubhouse.impl.dto.TeamPostRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostCommentRequest;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostRequest;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.repository.TeamPostCommentRepository;
import nu.borjessons.clubhouse.impl.repository.TeamPostRepository;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
import nu.borjessons.clubhouse.impl.service.TeamPostService;
import nu.borjessons.clubhouse.impl.util.ClubhouseUtils;

@Service
@RequiredArgsConstructor
public class TeamPostServiceImpl implements TeamPostService {
  private static TeamPost createTeamPost(ClubUser clubUser, Team team, TeamPostRequest teamPostRequest) {
    TeamPost teamPost = new TeamPost();
    teamPost.setTitle(teamPostRequest.title());
    teamPost.setBody(teamPostRequest.body());
    teamPost.setClubUser(clubUser);
    teamPost.setTeam(team);
    return teamPost;
  }

  private static void updateTeamPost(TeamPost teamPost, TeamPostRequest teamPostRequest) {
    teamPost.setTitle(teamPostRequest.title());
    teamPost.setBody(teamPostRequest.body());
  }

  private final ClubUserRepository clubUserRepository;
  private final TeamPostCommentRepository teamPostCommentRepository;
  private final TeamPostRepository teamPostRepository;
  private final TeamRepository teamRepository;

  @Override
  public TeamPostRecord createComment(User principal, String clubId, TeamPostId teamPostId, TeamPostCommentRequest teamPostCommentRequest) {
    ClubUser clubUser = getClubUser(principal, clubId);
    TeamPostComment teamPostComment = new TeamPostComment();
    teamPostComment.setComment(teamPostCommentRequest.comment());
    teamPostComment.setClubUser(clubUser);

    TeamPost teamPost = teamPostRepository.findByTeamPostId(teamPostId)
        .orElseThrow(ClubhouseUtils.createNotFoundExceptionSupplier("team post not found: " + teamPostId));
    teamPost.addComment(teamPostComment);
    return new TeamPostRecord(teamPostRepository.save(teamPost));
  }

  @Transactional
  @Override
  public TeamPostRecord createPost(User user, String clubId, String teamId, TeamPostRequest teamPostRequest) {
    ClubUser clubUser = getClubUser(user, clubId);
    Team team = teamRepository.findByTeamId(teamId).orElseThrow(ClubhouseUtils.createNotFoundExceptionSupplier("Team not found " + teamId));

    return new TeamPostRecord(teamPostRepository.save(createTeamPost(clubUser, team, teamPostRequest)));
  }

  @Transactional
  @Override
  public void deletePost(TeamPost teamPost) {
    teamPostRepository.delete(teamPost);
  }

  @Transactional
  @Override
  public void deleteTeamPostComment(TeamPostComment teamPostComment) {
    teamPostCommentRepository.delete(teamPostComment);
  }

  @Override
  public int getCommentSize(TeamPostId teamPostId) {
    return teamPostCommentRepository.countByTeamPostId(teamPostId);
  }

  @Override
  public TeamPostRecord getPost(TeamPostId teamPostId) {
    return new TeamPostRecord(
        teamPostRepository.findByTeamPostId(teamPostId).orElseThrow(ClubhouseUtils.createNotFoundExceptionSupplier("team post not found: " + teamPostId)));
  }

  @Override
  public Collection<TeamPostRecord> getPosts(String teamId, PageRequest pageRequest) {
    return teamPostRepository.findByTeamPostByTeamId(teamId, pageRequest).stream().map(TeamPostRecord::new).toList();
  }

  @Override
  public int getSize(String teamId) {
    return teamPostRepository.countByTeamId(teamId);
  }

  @Override
  public Collection<TeamPostCommentRecord> getTeamPostComments(TeamPostId teamPostId, PageRequest pageRequest) {
    return teamPostCommentRepository.findByTeamPostId(teamPostId, pageRequest).stream().map(TeamPostCommentRecord::new).toList();
  }

  @Override
  public TeamPostRecord toggleSticky(TeamPostId teamPostId) {
    TeamPost teamPost = teamPostRepository.findByTeamPostId(teamPostId)
        .orElseThrow(ClubhouseUtils.createNotFoundExceptionSupplier("Post not found: " + teamPostId));
    teamPost.setSticky(!teamPost.isSticky());
    return new TeamPostRecord(teamPostRepository.save(teamPost));
  }

  @Transactional
  @Override
  public TeamPostRecord updateComment(TeamPostComment teamPostComment, TeamPostCommentRequest teamPostCommentRequest) {
    teamPostComment.setComment(teamPostCommentRequest.comment());
    return new TeamPostRecord(teamPostCommentRepository.save(teamPostComment).getTeamPost());
  }

  @Override
  public TeamPostRecord updatePost(TeamPost teamPost, TeamPostRequest teamPostRequest) {
    updateTeamPost(teamPost, teamPostRequest);

    return new TeamPostRecord(teamPostRepository.save(teamPost));
  }

  private ClubUser getClubUser(User principal, String clubId) {
    return clubUserRepository.findByClubIdAndUserId(clubId, principal.getId())
        .orElseThrow(ClubhouseUtils.createNotFoundExceptionSupplier("user not found: " + principal.getUserId()));
  }

  private TeamPostComment getTeamPostComment(long teamPostCommentId, ClubUser clubUser) {
    return teamPostCommentRepository.findByIdAndClubUser(teamPostCommentId, clubUser)
        .orElseThrow(ClubhouseUtils.createNotFoundExceptionSupplier("teamPostCommentId not found: " + teamPostCommentId));
  }
}
