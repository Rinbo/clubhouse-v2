package nu.borjessons.clubhouse.impl.service;

import java.util.List;
import java.util.Set;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.TeamDto;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;

public interface TeamService {
  TeamDto addMemberToTeam(String clubId, String teamId, UserId userId);

  TeamDto updateTeamMembers(String clubId, String teamId, List<UserId> userIds);

  TeamDto createTeam(String clubId, TeamRequestModel teamModel);

  void removeMemberFromTeam(String clubId, String teamId, UserId userId);

  TeamDto removeLeaderFromTeam(String clubId, String teamId, UserId userId);

  TeamDto updateTeam(String clubId, String teamId, TeamRequestModel teamModel);

  Set<TeamDto> getTeamsByUserId(String clubId, UserId userId);
}
