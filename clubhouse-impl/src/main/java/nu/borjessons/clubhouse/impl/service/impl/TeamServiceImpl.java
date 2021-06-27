package nu.borjessons.clubhouse.impl.service.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.TeamDTO;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
import nu.borjessons.clubhouse.impl.service.ClubService;
import nu.borjessons.clubhouse.impl.service.TeamService;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {
  private final ClubService clubService;
  private final TeamRepository teamRepository;

  public TeamDTO addMemberToTeam(String clubId, String teamId, String userId) {
    Club club = clubService.getClubByClubId(clubId);
    Team team = club.getTeamByTeamId(teamId).orElseThrow();
    User user = club.getUser(userId).orElseThrow();
    team.addMember(user);
    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public TeamDTO getTeamById(String teamId) {
    return new TeamDTO(teamRepository.findByTeamId(teamId).orElseThrow());
  }

  @Override
  public void removeUsersFromAllTeams(Set<User> users, Club club) {
    Set<Team> teams = club.getTeams();
    teams.forEach(team -> removeUsers(users, team));

    teamRepository.saveAll(teams);
  }

  @Override
  public TeamDTO updateTeamMembers(String clubId, String teamId, Set<String> userIds) {
    Club club = clubService.getClubByClubId(clubId);
    Team team = club.getTeamByTeamId(teamId).orElseThrow();

    Set<User> members = club
        .getUsers()
        .stream()
        .filter(user -> userIds.contains(user.getUserId()))
        .collect(Collectors.toSet());

    team.setMembers(members);
    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public TeamDTO createTeam(String clubId, TeamRequestModel teamModel) {
    Club club = clubService.getClubByClubId(clubId);
    Set<User> leaders = getClubLeaders(teamModel.getLeaderIds(), club);

    Team team = new Team();
    team.setName(teamModel.getName());
    team.setMinAge(teamModel.getMinAge());
    team.setMaxAge(teamModel.getMaxAge());
    team.setLeaders(leaders);

    club.addTeam(team);
    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public TeamDTO removeMemberFromTeam(String clubId, String teamId, String userId) {
    Club club = clubService.getClubByClubId(clubId);
    Team team = club.getTeamByTeamId(teamId).orElseThrow();
    User user = club.getUser(userId).orElseThrow();
    team.removeMember(user);
    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public TeamDTO removeLeaderFromTeam(String clubId, String teamId, String userId) {
    Club club = clubService.getClubByClubId(clubId);
    Team team = club.getTeamByTeamId(teamId).orElseThrow();
    User leader = club.getUser(userId).orElseThrow();
    team.removeLeader(leader);
    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public TeamDTO updateTeam(String clubId, String teamId, TeamRequestModel teamModel) {
    Club club = clubService.getClubByClubId(clubId);
    Team team = club.getTeamByTeamId(teamId).orElseThrow();
    Set<User> leaders = getClubLeaders(teamModel.getLeaderIds(), club);

    team.setName(teamModel.getName());
    team.setMinAge(teamModel.getMinAge());
    team.setMaxAge(teamModel.getMaxAge());
    team.setLeaders(leaders);

    club.addTeam(team);
    return new TeamDTO(teamRepository.save(team));
  }

  private Set<User> getClubLeaders(Set<String> leaderIds, Club club) {
    return club.getTeams()
        .stream()
        .map(Team::getLeaders)
        .flatMap(Set::stream)
        .filter(user -> leaderIds.contains(user.getUserId()))
        .collect(Collectors.toSet());
  }

  private void removeUsers(Set<User> users, Team team) {
    for (User user : users) {
      team.removeMember(user);
      team.removeLeader(user);
    }
  }
}
