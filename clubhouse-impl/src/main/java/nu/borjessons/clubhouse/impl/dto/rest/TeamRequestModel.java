package nu.borjessons.clubhouse.impl.dto.rest;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRequestModel {
  @NotNull(message = "LeaderId list cannot be null")
  private List<String> leaderIds = new ArrayList<>();

  @NotNull(message = "Maximum age limit cannot be null")
  @Min(0)
  @Max(100)
  private int maxAge;

  @NotNull(message = "Minimum age limit cannot be null")
  @Min(0)
  @Max(100)
  private int minAge;

  @NotNull(message = "Team name cannot be null")
  @Size(min = 2, message = "Team name must consist of at least two characters")
  private String name;
}
