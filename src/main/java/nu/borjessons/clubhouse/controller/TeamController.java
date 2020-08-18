package nu.borjessons.clubhouse.controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.data.Team;
import nu.borjessons.clubhouse.dto.TeamDTO;

@RequestMapping("/teams")
@RequiredArgsConstructor
@RestController
public class TeamController extends AbstractController {
	
	@GetMapping("/principal/{teamId}")
	public TeamDTO getTeam(@PathVariable String teamId) {
		Optional<Team> maybeTeam = getPrincipal().getActiveClub().getTeams().stream().filter(team -> team.getTeamId().equals(teamId)).findFirst();
		Team team = getOrThrow(maybeTeam, Team.class.getSimpleName(), teamId);
		return new TeamDTO(team);
	}

}
