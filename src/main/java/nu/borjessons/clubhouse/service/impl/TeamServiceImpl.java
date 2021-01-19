package nu.borjessons.clubhouse.service.impl;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.TeamRequestModel;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.Team;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.TeamDTO;
import nu.borjessons.clubhouse.repository.ClubRepository;
import nu.borjessons.clubhouse.repository.TeamRepository;
import nu.borjessons.clubhouse.service.ClubhouseAbstractService;
import nu.borjessons.clubhouse.service.TeamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl extends ClubhouseAbstractService implements TeamService {

  private final TeamRepository teamRepository;
  private final ClubRepository clubRepository;

  @Override
  public Team getTeamById(String teamId) {
    return getOptional(teamRepository.findByTeamId(teamId), Team.class, teamId);
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
    clubRepository.save(club);

    return new TeamDTO(team);
  }

  @Override
  public TeamDTO addMemberToTeam(User member, Team team) {
    team.addMember(member);
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
    teams.forEach(
        team -> {
          for (User user : users) {
            team.removeMember(user);
            team.removeLeader(user);
          }
        });
    teamRepository.saveAll(teams);
  }

  @Override
  public TeamDTO updateTeam(
      Club club, String teamId, TeamRequestModel teamModel, Set<User> leaders) {
    Team team =
        club.getTeams().stream()
            .filter(t -> t.getTeamId().equals(teamId))
            .findFirst()
            .orElseThrow();
    team.setName(teamModel.getName());
    team.setLeaders(leaders);
    team.setMaxAge(teamModel.getMaxAge());
    team.setMinAge(teamModel.getMinAge());
    clubRepository.save(club);
    return new TeamDTO(team);
  }
}
