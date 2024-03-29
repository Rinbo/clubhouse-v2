package nu.borjessons.clubhouse.impl.dto.rest;

import java.util.Set;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import nu.borjessons.clubhouse.impl.dto.Role;

@Getter
@Setter
public class AdminUpdateUserModel extends UpdateUserModel {
  @NotNull(message = "You must provide roles")
  private Set<Role> roles;
}
