package nu.borjessons.clubhouse.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.data.Team;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.TeamDTO;
import nu.borjessons.clubhouse.repository.TeamRepository;
import nu.borjessons.clubhouse.service.ClubhouseAbstractService;
import nu.borjessons.clubhouse.service.TeamService;

@RequiredArgsConstructor
@Service
public class TeamServiceImpl extends ClubhouseAbstractService implements TeamService {

	private final TeamRepository teamRepository;
	
	@Override
	public Team getTeamById(String teamId) {
		return getOptional(teamRepository.findByTeamId(teamId), Team.class, teamId);
	}

	@Override
	public TeamDTO joinTeam(User principal, Team optional) {
		// TODO Auto-generated method stub
		return null;
	}

}
