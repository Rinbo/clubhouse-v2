package nu.borjessons.clubhouse.impl.dto.rest;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRequestModel {
  private String description;

  @NotNull(message = "LeaderIds cannot be null")
  private List<String> leaderIds = new ArrayList<>();

  @NotNull(message = "MemberIds cannot be null")
  private List<String> memberIds = new ArrayList<>();

  @NotNull(message = "Team name cannot be null")
  @Size(min = 2, message = "Team name must consist of at least two characters")
  private String name;

  private List<TrainingTimeRequest> trainingTimes = new ArrayList<>();
}
