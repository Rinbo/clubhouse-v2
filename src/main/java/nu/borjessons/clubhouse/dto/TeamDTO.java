package nu.borjessons.clubhouse.dto;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import nu.borjessons.clubhouse.data.Team;

@Getter
public class TeamDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final String teamId;
	private final String name;
	private final int minAge;
	private final int maxAge;
	private final Set<BaseUserDTO> members;
	private final Set<BaseUserDTO> leaders;
	
	public TeamDTO(Team team) {
		teamId = team.getTeamId();
		name = team.getName();
		minAge = team.getMinAge();
		maxAge = team.getMaxAge();
		members = team.getMembers().stream().map(BaseUserDTO::new).collect(Collectors.toSet());
		leaders = team.getLeaders().stream().map(BaseUserDTO::new).collect(Collectors.toSet());
	}
}
