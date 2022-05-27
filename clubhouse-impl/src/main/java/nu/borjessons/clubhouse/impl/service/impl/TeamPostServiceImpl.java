package nu.borjessons.clubhouse.impl.service.impl;

import java.util.NoSuchElementException;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.TeamPost;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.TeamPostRecord;
import nu.borjessons.clubhouse.impl.dto.rest.TeamPostRequest;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.repository.TeamPostRepository;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
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

  private final ClubUserRepository clubUserRepository;
  private final TeamPostRepository teamPostRepository;
  private final TeamRepository teamRepository;

  @Transactional
  @Override
  public TeamPostRecord createPost(User user, String clubId, String teamId, TeamPostRequest teamPostRequest) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, user.getId())
        .orElseThrow(notFoundExceptionSupplier("user not found: " + user.getUserId()));

    Team team = teamRepository.findByTeamId(teamId).orElseThrow(notFoundExceptionSupplier("team not found: " + teamId));

    return new TeamPostRecord(teamPostRepository.save(createTeamPost(clubUser, team, teamPostRequest)));
  }
}
