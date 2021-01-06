package nu.borjessons.clubhouse.controller.model.request;

import lombok.Getter;
import lombok.Setter;
import nu.borjessons.clubhouse.data.ClubRole;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
public class AdminUpdateUserModel extends UpdateUserModel {
  @NotNull(message = "You must provide roles")
  private Set<ClubRole.Role> roles;
}
