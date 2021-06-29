package nu.borjessons.clubhouse.impl.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.RoleEntity;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.dto.TeamDTO;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.repository.TeamRepository;
import nu.borjessons.clubhouse.impl.service.TeamService;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl implements TeamService {
  private final ClubRepository clubRepository;
  private final ClubUserRepository clubUserRepository;
  private final TeamRepository teamRepository;

  @Override
  @Transactional
  public TeamDTO addMemberToTeam(String clubId, String teamId, String userId) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId).orElseThrow();
    Team team = clubUser.getClub().getTeamByTeamId(teamId).orElseThrow();
    team.addMember(clubUser);
    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public TeamDTO getTeamById(String teamId) {
    return new TeamDTO(teamRepository.findByTeamId(teamId).orElseThrow());
  }

  /*
  @Override
  public void removeUsersFromAllTeams(Set<User> users, Club club) {
    Set<Team> teams = club.getTeams();
    teams.forEach(team -> removeUsers(users, team));

    teamRepository.saveAll(teams);
  }
  */

  @Override
  public TeamDTO updateTeamMembers(String clubId, String teamId, List<String> userIds) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    Team team = club.getTeamByTeamId(teamId).orElseThrow();

    team.setMembers(clubUserRepository.findByClubIdAndUserIds(clubId, userIds));
    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public TeamDTO createTeam(String clubId, TeamRequestModel teamModel) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    List<ClubUser> leaders = getClubLeaders(teamModel.getLeaderIds(), club);

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
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId).orElseThrow();
    Team team = clubUser.getClub().getTeamByTeamId(teamId).orElseThrow();
    team.removeMember(clubUser);
    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public TeamDTO removeLeaderFromTeam(String clubId, String teamId, String userId) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId).orElseThrow();
    Team team = clubUser.getClub().getTeamByTeamId(teamId).orElseThrow();
    team.removeLeader(clubUser);
    return new TeamDTO(teamRepository.save(team));
  }

  @Override
  public TeamDTO updateTeam(String clubId, String teamId, TeamRequestModel teamModel) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    Team team = club.getTeamByTeamId(teamId).orElseThrow();
    List<ClubUser> leaders = getClubLeaders(teamModel.getLeaderIds(), club);

    team.setName(teamModel.getName());
    team.setMinAge(teamModel.getMinAge());
    team.setMaxAge(teamModel.getMaxAge());
    team.setLeaders(leaders);

    club.addTeam(team);
    return new TeamDTO(teamRepository.save(team));
  }

  private boolean validateIsLeader(ClubUser clubUser) {
    return clubUser.getRoles()
        .stream()
        .map(RoleEntity::getName)
        .collect(Collectors.toList())
        .contains(Role.LEADER);
  }

  private List<ClubUser> getClubLeaders(List<String> leaderIds, Club club) {
    return club.getClubUsers(leaderIds)
        .stream()
        .filter(this::validateIsLeader)
        .collect(Collectors.toList());
  }

/*  private void removeUsers(Set<User> users, Team team) {
    for (User user : users) {
      team.removeMember(user);
      team.removeLeader(user);
    }
  }*/
}
