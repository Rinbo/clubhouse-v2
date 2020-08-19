package nu.borjessons.clubhouse.service;

import nu.borjessons.clubhouse.data.Team;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.TeamDTO;

public interface TeamService {
	
	Team getTeamById(String teamId);

	TeamDTO joinTeam(User principal, Team optional);

}
