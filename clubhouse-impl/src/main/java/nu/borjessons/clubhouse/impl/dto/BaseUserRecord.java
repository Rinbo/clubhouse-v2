package nu.borjessons.clubhouse.impl.dto;

import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.util.Validate;

public record BaseUserRecord(
    String userId,
    String firstName,
    String lastName,
    String dateOfBirth,
    ImageTokenId imageTokenId,
    boolean managedUser) {

  public BaseUserRecord {
    Validate.notEmpty(userId, "userId");
    Validate.notEmpty(firstName, "firstName");
    Validate.notEmpty(lastName, "lastName");
    Validate.notEmpty(dateOfBirth, "dateOfBirth");
  }

  public BaseUserRecord(User user) {
    this(
        user.getUserId().toString(),
        user.getFirstName(),
        user.getLastName(),
        user.getDateOfBirth().toString(),
        user.getImageTokenId(),
        user.isManagedAccount());
  }
}
