package nu.borjessons.clubhouse.impl.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.RoleEntity;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.dto.TeamDto;
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
  public TeamDto addMemberToTeam(String clubId, String teamId, UserId userId) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId.toString()).orElseThrow();
    Team team = clubUser.getClub().getTeamByTeamId(teamId).orElseThrow();
    team.addMember(clubUser);
    return TeamDto.create(teamRepository.save(team));
  }

  @Override
  @Transactional
  public TeamDto updateTeamMembers(String clubId, String teamId, List<UserId> userIds) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    Team team = club.getTeamByTeamId(teamId).orElseThrow();

    List<ClubUser> teamMembers = clubUserRepository.findByClubIdAndUserIds(clubId, userIds.stream().map(UserId::toString).toList());
    List.copyOf(team.getMembers()).forEach(team::removeMember);
    teamMembers.forEach(team::addMember);

    return TeamDto.create(teamRepository.save(team));
  }

  @Override
  @Transactional
  public TeamDto createTeam(String clubId, TeamRequestModel teamModel) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    List<ClubUser> leaders = getClubLeaders(teamModel.getLeaderIds().stream().map(UserId::new).toList(), club);

    Team team = new Team();
    team.setName(teamModel.getName());
    team.setMinAge(teamModel.getMinAge());
    team.setMaxAge(teamModel.getMaxAge());
    leaders.forEach(team::addLeader);

    club.addTeam(team);
    final Team savedTeam = teamRepository.save(team);
    return TeamDto.create(savedTeam);
  }

  @Override
  @Transactional
  public void removeMemberFromTeam(String clubId, String teamId, UserId userId) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId.toString()).orElseThrow();
    Team team = clubUser.getClub().getTeamByTeamId(teamId).orElseThrow();
    team.removeMember(clubUser);
    teamRepository.save(team);
  }

  @Override
  @Transactional
  public TeamDto removeLeaderFromTeam(String clubId, String teamId, UserId userId) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId.toString()).orElseThrow();
    Team team = clubUser.getClub().getTeamByTeamId(teamId).orElseThrow();
    team.removeLeader(clubUser);
    return TeamDto.create(teamRepository.save(team));
  }

  @Override
  @Transactional
  public TeamDto updateTeam(String clubId, String teamId, TeamRequestModel teamModel) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    Team team = club.getTeamByTeamId(teamId).orElseThrow();
    List<ClubUser> leaders = getClubLeaders(teamModel.getLeaderIds().stream().map(UserId::new).toList(), club);

    team.setName(teamModel.getName());
    team.setMinAge(teamModel.getMinAge());
    team.setMaxAge(teamModel.getMaxAge());
    List.copyOf(team.getLeaders()).forEach(team::removeLeader);
    leaders.forEach(team::addLeader);

    club.addTeam(team);
    return TeamDto.create(teamRepository.save(team));
  }

  @Override
  @Transactional
  public Set<TeamDto> getTeamsByUserId(String clubId, UserId userId) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId.toString()).orElseThrow();
    return clubUser.getJoinedTeams()
        .stream()
        .map(TeamDto::create)
        .collect(Collectors.toSet());
  }

  private ClubUser validateIsLeader(ClubUser clubUser) {
    boolean isLeader = clubUser.getRoles()
        .stream()
        .map(RoleEntity::getName)
        .toList()
        .contains(Role.LEADER);

    if (!isLeader) throw new IllegalArgumentException(String.format("User with id %s does not have role leader", clubUser.getUser().getUserId()));
    return clubUser;
  }

  private List<ClubUser> getClubLeaders(List<UserId> leaderIds, Club club) {
    return club.getClubUsers(leaderIds)
        .stream()
        .map(this::validateIsLeader)
        .toList();
  }
}
