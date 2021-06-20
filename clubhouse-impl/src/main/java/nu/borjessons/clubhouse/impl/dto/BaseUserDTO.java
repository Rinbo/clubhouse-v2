package nu.borjessons.clubhouse.impl.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nu.borjessons.clubhouse.impl.data.User;

@Getter
@NoArgsConstructor
public class BaseUserDTO {
  private String userId;
  private String firstName;
  private String lastName;
  private String dateOfBirth;

  public BaseUserDTO(User user) {
    userId = user.getUserId();
    firstName = user.getFirstName();
    lastName = user.getLastName();
    dateOfBirth = user.getDateOfBirth().toString();
  }
}
