package nu.borjessons.clubhouse.impl.service.impl;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.controller.model.request.TeamRequestModel;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.TeamDTO;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
import nu.borjessons.clubhouse.impl.service.ClubService;
import nu.borjessons.clubhouse.impl.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {

  private final TeamRepository teamRepository;
  private final ClubService clubService;

  @Override
  public Team getTeamById(String teamId) {
    return teamRepository.findByTeamId(teamId).orElseThrow();
  }

  @Override
  @Transactional
  public TeamDTO createTeam(Club club, TeamRequestModel teamModel, Set<User> leaders) {
    Team team = new Team();
    team.setName(teamModel.getName());
    team.setMinAge(teamModel.getMinAge());
    team.setMaxAge(teamModel.getMaxAge());
    team.setLeaders(leaders);
    club.addTeam(team);

    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public TeamDTO addMemberToTeam(User member, Team team) {
    team.addMember(member);
    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public TeamDTO updateTeamMembers(Club club, String teamId, Set<String> memberIds) {
    Team team = club.getTeams()
        .stream()
        .filter(t -> t.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();

    Set<User> members = club
        .getUsers()
        .stream()
        .filter(user -> memberIds.contains(user.getUserId()))
        .collect(Collectors.toSet());

    team.setMembers(members);

    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public void removeMemberFromTeam(User member, Team team) {
    team.removeMember(member);
    teamRepository.save(team);
  }

  @Override
  public TeamDTO addLeaderToTeam(User leader, Team team) {
    team.addLeader(leader);
    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public TeamDTO removeLeaderFromTeam(User leader, Team team) {
    team.removeLeader(leader);
    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public void removeUsersFromAllTeams(Set<User> users, Club club) {
    Set<Team> teams = club.getTeams();
    teams.forEach(team -> removeUsers(users, team));

    teamRepository.saveAll(teams);
  }

  @Override
  public TeamDTO updateTeam(Club club, String teamId, TeamRequestModel teamModel, Set<User> leaders) {
    Team team = club
        .getTeams()
        .stream()
        .filter(t -> t.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();

    team.setName(teamModel.getName());
    team.setLeaders(leaders);
    team.setMaxAge(teamModel.getMaxAge());
    team.setMinAge(teamModel.getMinAge());

    return new TeamDTO(teamRepository.save(team));
  }

  private void removeUsers(Set<User> users, Team team) {
    for (User user : users) {
      team.removeMember(user);
      team.removeLeader(user);
    }
  }
}
