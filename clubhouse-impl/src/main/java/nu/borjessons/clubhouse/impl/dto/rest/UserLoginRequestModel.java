package nu.borjessons.clubhouse.impl.dto.rest;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestModel {

  @NotNull(message = "Password cannot be null")
  private String password;
  @NotNull(message = "Username cannot be null")
  private String username;
}
