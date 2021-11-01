package nu.borjessons.clubhouse.impl.dto;

import java.time.LocalDate;

import nu.borjessons.clubhouse.impl.data.User;

public record BaseUserDTO(String userId, String firstName, String lastName, LocalDate dateOfBirth) {
  public BaseUserDTO(User user) {
    this(user.getUserId(), user.getFirstName(), user.getLastName(), user.getDateOfBirth());
  }
}
