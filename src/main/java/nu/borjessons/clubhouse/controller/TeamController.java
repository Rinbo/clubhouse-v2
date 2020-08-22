package nu.borjessons.clubhouse.controller;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.CreateTeamModel;
import nu.borjessons.clubhouse.data.Team;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.TeamDTO;
import nu.borjessons.clubhouse.service.TeamService;

@RequestMapping("/teams")
@RequiredArgsConstructor
@RestController
public class TeamController extends AbstractController {
	
	private final TeamService teamService;
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/principal/{teamId}")
	public TeamDTO getTeam(@PathVariable String teamId) {
		Optional<Team> maybeTeam = getPrincipal().getActiveClub().getTeams().stream().filter(team -> team.getTeamId().equals(teamId)).findFirst();
		Team team = getOptional(maybeTeam, Team.class, teamId);
		return new TeamDTO(team);
	}
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/principal")
	public Set<TeamDTO> getTeams() {
		Set<Team> teams = getPrincipal().getActiveClub().getTeams();
		return teams.stream().map(TeamDTO::new).collect(Collectors.toSet());
	}
	
	@PreAuthorize("hasRole('USER')")
	@PutMapping("/principal/join")
	public TeamDTO editTeam(@RequestParam String teamId) {
		User principal = getPrincipal();
		Set<Team> teams = principal.getActiveClub().getTeams();
		Optional<Team> maybeTeam = teams.stream().filter(team -> team.getTeamId().equals(teamId)).findFirst();
		return teamService.joinTeam(principal, getOptional(maybeTeam));
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public TeamDTO createTeam(@RequestBody CreateTeamModel teamModel) {
		return teamService.createTeam(getPrincipal().getActiveClub(), teamModel);
	}

}
