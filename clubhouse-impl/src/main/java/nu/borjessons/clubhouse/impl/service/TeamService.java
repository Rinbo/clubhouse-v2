package nu.borjessons.clubhouse.impl.service;

import java.util.List;
import java.util.Set;

import nu.borjessons.clubhouse.impl.dto.TeamDTO;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;

public interface TeamService {
  TeamDTO addMemberToTeam(String clubId, String teamId, String userId);

  TeamDTO updateTeamMembers(String clubId, String teamId, List<String> userIds);

  TeamDTO createTeam(String clubId, TeamRequestModel teamModel);

  TeamDTO removeMemberFromTeam(String clubId, String teamId, String userId);

  TeamDTO removeLeaderFromTeam(String clubId, String teamId, String userId);

  TeamDTO updateTeam(String clubId, String teamId, TeamRequestModel teamModel);

  Set<TeamDTO> getTeamsByUserId(String clubId, String userId);
}
