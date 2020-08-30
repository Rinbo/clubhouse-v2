package nu.borjessons.clubhouse.service;

import java.util.Set;

import nu.borjessons.clubhouse.controller.model.request.CreateTeamModel;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.Team;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.TeamDTO;

public interface TeamService {
	
	Team getTeamById(String teamId);

	TeamDTO addMemberToTeam(User memeber, Team team);
	
	TeamDTO addLeaderToTeam(User leader, Team team);
	
	TeamDTO createTeam(Club club, CreateTeamModel teamModel);

	void removeMemberFromTeam(User memeber, Team team);
	
	void removeLeaderFromTeam(User leader, Team team);
	
	void removeUsersFromAllTeams(Set<User> users, Club club);
}
