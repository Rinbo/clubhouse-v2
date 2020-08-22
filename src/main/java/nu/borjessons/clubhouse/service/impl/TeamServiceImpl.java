package nu.borjessons.clubhouse.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.CreateTeamModel;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.Team;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.TeamDTO;
import nu.borjessons.clubhouse.repository.ClubRepository;
import nu.borjessons.clubhouse.repository.TeamRepository;
import nu.borjessons.clubhouse.service.ClubhouseAbstractService;
import nu.borjessons.clubhouse.service.TeamService;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl extends ClubhouseAbstractService implements TeamService {

	private final TeamRepository teamRepository;
	private final ClubRepository clubRepository;
	
	@Override
	public Team getTeamById(String teamId) {
		return getOptional(teamRepository.findByTeamId(teamId), Team.class, teamId);
	}

	@Override
	public TeamDTO joinTeam(User principal, Team team) {
		team.addMember(principal);
		return new TeamDTO(teamRepository.save(team));
	}

	@Override
	public TeamDTO createTeam(Club club, CreateTeamModel teamModel) {
		Team team  = new Team();
		team.setName(teamModel.getName());
		team.setMinAge(teamModel.getMinAge());
		team.setMaxAge(teamModel.getMaxAge());
		
		club.addTeam(team);
		clubRepository.save(club);
		
		return new TeamDTO(team);
	}
}
