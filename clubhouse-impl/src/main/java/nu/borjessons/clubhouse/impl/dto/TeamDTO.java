package nu.borjessons.clubhouse.impl.dto;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.ToString;
import nu.borjessons.clubhouse.impl.data.Team;

@Getter
@ToString
public class TeamDTO implements Serializable {
  private static final long serialVersionUID = 1L;

  private final String teamId;
  private final String name;
  private final int minAge;
  private final int maxAge;
  private final Set<ClubUserDTO> members;
  private final Set<ClubUserDTO> leaders;

  public TeamDTO(Team team) {
    teamId = team.getTeamId();
    name = team.getName();
    minAge = team.getMinAge();
    maxAge = team.getMaxAge();
    members = team.getMembers().stream().map(ClubUserDTO::new).collect(Collectors.toSet());
    leaders = team.getLeaders().stream().map(ClubUserDTO::new).collect(Collectors.toSet());
  }
}
