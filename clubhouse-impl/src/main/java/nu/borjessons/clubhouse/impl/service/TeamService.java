package nu.borjessons.clubhouse.impl.service;

import java.util.List;
import java.util.Set;

import nu.borjessons.clubhouse.impl.dto.TeamDto;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;

public interface TeamService {
  TeamDto addMemberToTeam(String clubId, String teamId, String userId);

  TeamDto updateTeamMembers(String clubId, String teamId, List<String> userIds);

  TeamDto createTeam(String clubId, TeamRequestModel teamModel);

  void removeMemberFromTeam(String clubId, String teamId, String userId);

  TeamDto removeLeaderFromTeam(String clubId, String teamId, String userId);

  TeamDto updateTeam(String clubId, String teamId, TeamRequestModel teamModel);

  Set<TeamDto> getTeamsByUserId(String clubId, String userId);
}
