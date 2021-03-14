package nu.borjessons.clubhouse.impl.service;

import nu.borjessons.clubhouse.impl.controller.model.request.TeamRequestModel;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.TeamDTO;

import java.util.Set;

public interface TeamService {

  Team getTeamById(String teamId);

  TeamDTO addMemberToTeam(User member, Team team);

  TeamDTO updateTeamMembers(Club club, String teamId, Set<String> memberIds);

  TeamDTO addLeaderToTeam(User leader, Team team);

  TeamDTO createTeam(Club club, TeamRequestModel teamModel, Set<User> leaders);

  void removeMemberFromTeam(User member, Team team);

  TeamDTO removeLeaderFromTeam(User leader, Team team);

  void removeUsersFromAllTeams(Set<User> users, Club club);

  TeamDTO updateTeam(Club club, String teamId, TeamRequestModel teamModel, Set<User> leaders);
}
