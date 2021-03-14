package nu.borjessons.clubhouse.impl.controller.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestModel {

  @NotNull(message = "Username cannot be null")
  private String username;

  @NotNull(message = "Password cannot be null")
  private String password;
}
