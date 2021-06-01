package nu.borjessons.clubhouse.impl.controller.model.request;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamRequestModel implements Serializable {
  private static final long serialVersionUID = 1L;

  @NotNull(message = "LeaderId list cannot be null")
  private Set<String> leaderIds = new HashSet<>();

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
