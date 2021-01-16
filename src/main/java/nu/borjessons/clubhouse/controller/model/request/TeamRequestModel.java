package nu.borjessons.clubhouse.controller.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class TeamRequestModel implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull(message = "Team name cannot be null")
  @Size(min = 2, message = "Team name must consist of at least two characters")
  private String name;

  @NotNull(message = "Minimum age limit cannot be null")
  @Min(5)
  @Max(18)
  private int minAge;

  @NotNull(message = "Maximum age limit cannot be null")
  @Min(5)
  @Max(100)
  private int maxAge;

  @NotNull(message = "LeaderId list cannot be null")
  private Set<String> leaderIds = new HashSet<>();
}
