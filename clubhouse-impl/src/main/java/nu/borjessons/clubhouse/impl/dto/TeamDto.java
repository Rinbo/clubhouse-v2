package nu.borjessons.clubhouse.impl.dto;

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
public final class TeamDto {
  public static TeamDto create(Team team) {
    return TeamDto.builder()
        .description(team.getDescription())
        .members(team.getMembers().stream().map(ClubUserDto::new).collect(Collectors.toSet()))
        .leaders(team.getLeaders().stream().map(ClubUserDto::new).collect(Collectors.toSet()))
        .name(team.getName())
        .teamId(team.getTeamId())
        .trainingTimes(
            team.getTrainingTimes()
                .stream()
                .map(TrainingTimeRecord::new)
                .sorted(TrainingTimeRecord::localTimeComparator)
                .toList())
        .build();
  }

  private final String description;
  private final Set<ClubUserDto> leaders;
  private final Set<ClubUserDto> members;
  private final String name;
  private final String teamId;
  private final List<TrainingTimeRecord> trainingTimes;
}
