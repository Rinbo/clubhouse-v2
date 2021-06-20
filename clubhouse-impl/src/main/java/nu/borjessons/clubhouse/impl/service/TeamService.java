package nu.borjessons.clubhouse.impl.service;

import java.util.Set;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;

public interface TeamService {
  Team addMemberToTeam(String clubId, String teamId, String userId);

  Team getTeamById(String teamId);

  void removeUsersFromAllTeams(Set<User> users, Club club);

  Team updateTeamMembers(String clubId, String teamId, Set<String> userIds);

  Team createTeam(String clubId, TeamRequestModel teamModel);

  Team removeMemberFromTeam(String clubId, String teamId, String userId);

  Team removeLeaderFromTeam(String clubId, String teamId, String userId);

  Team updateTeam(String clubId, String teamId, TeamRequestModel teamModel);
}
