package nu.borjessons.clubhouse.impl.dto;

import nu.borjessons.clubhouse.impl.data.User;

public record BaseUserRecord(String userId, String firstName, String lastName, String dateOfBirth, boolean managedUser) {
  public BaseUserRecord(User user) {
    this(user.getUserId().toString(), user.getFirstName(), user.getLastName(), user.getDateOfBirth().toString(), user.isManagedAccount());
  }
}
