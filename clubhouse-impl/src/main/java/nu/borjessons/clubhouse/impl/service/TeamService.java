package nu.borjessons.clubhouse.impl.service;

import java.util.Set;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.TeamDTO;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;

public interface TeamService {

  TeamDTO addLeaderToTeam(User leader, Team team);

  TeamDTO addMemberToTeam(User member, Team team);

  TeamDTO createTeam(Club club, TeamRequestModel teamModel, Set<User> leaders);

  Team getTeamById(String teamId);

  TeamDTO removeLeaderFromTeam(User leader, Team team);

  void removeMemberFromTeam(User member, Team team);

  void removeUsersFromAllTeams(Set<User> users, Club club);

  TeamDTO updateTeam(Club club, String teamId, TeamRequestModel teamModel, Set<User> leaders);

  TeamDTO updateTeamMembers(Club club, String teamId, Set<String> memberIds);
}
