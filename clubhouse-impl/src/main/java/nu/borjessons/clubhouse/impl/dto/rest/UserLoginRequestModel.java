package nu.borjessons.clubhouse.impl.dto.rest;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequestModel {
  @NotNull(message = "Password cannot be null")
  private String password;
  @NotNull(message = "Username cannot be null")
  private String username;

  public UserLoginRequestModel(String username, String password) {
    this.username = username;
    this.password = password;
  }
}
