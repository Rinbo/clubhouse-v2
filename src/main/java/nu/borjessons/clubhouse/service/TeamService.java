package nu.borjessons.clubhouse.service;

import nu.borjessons.clubhouse.controller.model.request.CreateTeamModel;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.Team;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.TeamDTO;

public interface TeamService {
	
	Team getTeamById(String teamId);

	TeamDTO joinTeam(User principal, Team team);
	
	TeamDTO createTeam(Club club, CreateTeamModel teamModel);
}
