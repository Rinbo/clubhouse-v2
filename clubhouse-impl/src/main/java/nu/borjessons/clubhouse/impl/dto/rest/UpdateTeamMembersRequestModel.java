package nu.borjessons.clubhouse.impl.dto.rest;

import java.io.Serializable;
import java.util.Set;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTeamMembersRequestModel implements Serializable {
  private static final long serialVersionUID = 2607222674635514324L;

  @NotNull(message = "Member List cannot be null")
  private Set<String> memberIds;

  @NotNull(message = "TeamId cannot be null")
  private String teamId;
}
