package nu.borjessons.clubhouse.impl.service;

import java.util.Set;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.TeamDTO;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;

public interface TeamService {
  TeamDTO addMemberToTeam(String clubId, String teamId, String userId);

  TeamDTO getTeamById(String teamId);

  void removeUsersFromAllTeams(Set<User> users, Club club);

  TeamDTO updateTeamMembers(String clubId, String teamId, Set<String> userIds);

  TeamDTO createTeam(String clubId, TeamRequestModel teamModel);

  TeamDTO removeMemberFromTeam(String clubId, String teamId, String userId);

  TeamDTO removeLeaderFromTeam(String clubId, String teamId, String userId);

  TeamDTO updateTeam(String clubId, String teamId, TeamRequestModel teamModel);
}
