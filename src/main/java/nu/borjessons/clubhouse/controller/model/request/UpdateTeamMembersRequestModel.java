package nu.borjessons.clubhouse.controller.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
public class UpdateTeamMembersRequestModel implements Serializable {
  @NotNull(message = "TeamId cannot be null")
  private String teamId;

  @NotNull(message = "Member List cannot be null")
  private Set<String> memberIds;
}
