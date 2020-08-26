package nu.borjessons.clubhouse.service;

import nu.borjessons.clubhouse.controller.model.request.CreateTeamModel;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.Team;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.TeamDTO;

public interface TeamService {
	
	Team getTeamById(String teamId);

	TeamDTO addUserToTeam(User user, Team team);
	
	TeamDTO addLeaderToTeam(User leader, Team team);
	
	TeamDTO createTeam(Club club, CreateTeamModel teamModel);

	void removeUserFromTeam(User user, Team team);
	
	void removeLeaderFromTeam(User leader, Team team);
}
