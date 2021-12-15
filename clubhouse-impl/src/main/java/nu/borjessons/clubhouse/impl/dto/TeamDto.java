package nu.borjessons.clubhouse.impl.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import nu.borjessons.clubhouse.impl.data.Team;

@Builder
@Getter
@ToString
public final class TeamDto implements Serializable {
  @Serial
  private static final long serialVersionUID = 1L;

  public static TeamDto create(Team team) {
    return TeamDto.builder()
        .teamId(team.getTeamId())
        .name(team.getName())
        .minAge(team.getMinAge())
        .maxAge(team.getMaxAge())
        .members(team.getMembers().stream().map(ClubUserDto::new).collect(Collectors.toSet()))
        .leaders(team.getLeaders().stream().map(ClubUserDto::new).collect(Collectors.toSet()))
        .trainingTimes(team.getTrainingTimes().stream().map(TrainingTimeRecord::new).toList())
        .build();
  }

  private final String teamId;
  private final String name;
  private final int minAge;
  private final int maxAge;
  private final Set<ClubUserDto> members;
  private final Set<ClubUserDto> leaders;
  private final List<TrainingTimeRecord> trainingTimes;
}
