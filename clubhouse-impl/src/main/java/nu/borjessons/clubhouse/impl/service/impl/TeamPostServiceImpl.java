package nu.borjessons.clubhouse.impl.service.impl;

import java.util.Collection;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

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
import nu.borjessons.clubhouse.impl.service.TeamPostService;

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

  private static Supplier<NoSuchElementException> notFoundExceptionSupplier(String message) {
    return () -> new NoSuchElementException(message);
  }

  private static void updateTeamPost(TeamPost teamPost, TeamPostRequest teamPostRequest) {
    teamPost.setTitle(teamPostRequest.title());
    teamPost.setBody(teamPostRequest.body());
  }

  private final ClubUserRepository clubUserRepository;
  private final TeamPostRepository teamPostRepository;
  private final TeamPostCommentRepository teamPostCommentRepository;

  @Transactional
  @Override
  public TeamPostRecord createPost(User user, String clubId, String teamId, TeamPostRequest teamPostRequest) {
    ClubUser clubUser = getClubUser(user, clubId);
    Team team = findClubUserTeam(clubUser, teamId);

    return new TeamPostRecord(teamPostRepository.save(createTeamPost(clubUser, team, teamPostRequest)));
  }

  @Override
  public Collection<TeamPostRecord> getPosts(String teamId, PageRequest pageRequest) {
    return teamPostRepository.findByTeamPostByTeamId(teamId, pageRequest).stream().map(TeamPostRecord::new).toList();
  }

  @Override
  public TeamPostRecord toggleSticky(TeamPostId teamPostId) {
    TeamPost teamPost = teamPostRepository.findByTeamPostId(teamPostId).orElseThrow(notFoundExceptionSupplier("Post not found: " + teamPostId));
    teamPost.setSticky(!teamPost.isSticky());
    return new TeamPostRecord(teamPostRepository.save(teamPost));
  }

  @Override
  @Transactional
  public TeamPostRecord updatePost(User principal, String clubId, String teamId, TeamPostId teamPostId, TeamPostRequest teamPostRequest) {
    ClubUser clubUser = getClubUser(principal, clubId);
    TeamPost teamPost = getTeamPost(clubUser, teamPostId);
    updateTeamPost(teamPost, teamPostRequest);

    return new TeamPostRecord(teamPostRepository.save(teamPost));
  }

  @Override
  public TeamPostRecord getPost(TeamPostId teamPostId) {
    return new TeamPostRecord(teamPostRepository.findByTeamPostId(teamPostId).orElseThrow(notFoundExceptionSupplier("team post not found: " + teamPostId)));
  }

  @Override
  public void deletePost(TeamPostId teamPostId) {
    teamPostRepository.deleteByTeamPostId(teamPostId);
  }

  @Transactional
  @Override
  public void deletePost(User principal, String clubId, TeamPostId teamPostId) {
    ClubUser clubUser = getClubUser(principal, clubId);
    TeamPost teamPost = getTeamPost(clubUser, teamPostId);
    teamPostRepository.delete(teamPost);
  }

  @Override
  public TeamPostRecord createComment(User principal, String clubId, TeamPostId teamPostId, TeamPostCommentRequest teamPostCommentRequest) {
    ClubUser clubUser = getClubUser(principal, clubId);
    TeamPostComment teamPostComment = new TeamPostComment();
    teamPostComment.setComment(teamPostCommentRequest.comment());
    teamPostComment.setClubUser(clubUser);

    TeamPost teamPost = teamPostRepository.findByTeamPostId(teamPostId).orElseThrow(notFoundExceptionSupplier("team post not found: " + teamPostId));
    teamPost.addComment(teamPostComment);
    return new TeamPostRecord(teamPostRepository.save(teamPost));
  }

  @Override
  @Transactional
  public TeamPostRecord updateComment(User principal, String clubId, long teamPostCommentId, TeamPostCommentRequest teamPostCommentRequest) {
    ClubUser clubUser = getClubUser(principal, clubId);
    TeamPostComment teamPostComment = getTeamPostComment(teamPostCommentId, clubUser);
    teamPostComment.setComment(teamPostCommentRequest.comment());
    return new TeamPostRecord(teamPostCommentRepository.save(teamPostComment).getTeamPost());
  }

  @Override
  public void deleteTeamPostComment(long teamPostCommentId) {
    teamPostCommentRepository.deleteById(teamPostCommentId);
  }

  @Override
  public void deleteTeamPostComment(User principal, String clubId, long teamPostCommentId) {
    ClubUser clubUser = getClubUser(principal, clubId);
    teamPostCommentRepository.delete(getTeamPostComment(teamPostCommentId, clubUser));
  }

  @Override
  public Collection<TeamPostCommentRecord> getTeamPostComments(TeamPostId teamPostId, PageRequest pageRequest) {
    return teamPostCommentRepository.findByTeamPostId(teamPostId, pageRequest).stream().map(TeamPostCommentRecord::new).toList();
  }

  private TeamPostComment getTeamPostComment(long teamPostCommentId, ClubUser clubUser) {
    return teamPostCommentRepository.findByIdAndClubUser(teamPostCommentId, clubUser)
        .orElseThrow(notFoundExceptionSupplier("teamPostCommentId not found: " + teamPostCommentId));
  }

  private Team findClubUserTeam(ClubUser clubUser, String teamId) {
    return clubUser.getJoinedTeams()
        .stream().filter(t -> t.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow(notFoundExceptionSupplier("team not found: " + teamId));
  }

  private TeamPost getTeamPost(ClubUser clubUser, TeamPostId teamPostId) {
    return teamPostRepository.findByTeamPostIdAndClubUser(teamPostId, clubUser)
        .orElseThrow(notFoundExceptionSupplier(String.format(Locale.ROOT, "Post with id %s not found for user %s", teamPostId, clubUser.getId())));
  }

  private ClubUser getClubUser(User principal, String clubId) {
    return clubUserRepository.findByClubIdAndUserId(clubId, principal.getId())
        .orElseThrow(notFoundExceptionSupplier("user not found: " + principal.getUserId()));
  }
}
