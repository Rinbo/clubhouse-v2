package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.TeamDto;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;

public interface TeamService {
  TeamDto addMemberToTeam(String clubId, String teamId, UserId userId);

  TeamDto createTeam(String clubId, TeamRequestModel teamModel);

  void deleteTeam(String teamId);

  Collection<TeamDto> getClubTeams(String clubId);

  TeamDto getTeam(String teamId);

  Set<TeamDto> getTeamsByUserId(String clubId, UserId userId);

  TeamDto removeLeaderFromTeam(String clubId, String teamId, UserId userId);

  void removeMemberFromTeam(String clubId, String teamId, UserId userId);

  TeamDto updateTeam(String clubId, String teamId, TeamRequestModel teamModel);

  TeamDto updateTeamMembers(String clubId, String teamId, List<UserId> userIds);
}
