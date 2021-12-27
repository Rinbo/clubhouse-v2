package nu.borjessons.clubhouse.impl.dto.rest;

import java.util.Set;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTeamMembersRequestModel {
  @NotNull(message = "Member List cannot be null")
  private Set<String> memberIds;

  @NotNull(message = "TeamId cannot be null")
  private String teamId;
}
