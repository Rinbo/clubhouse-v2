package nu.borjessons.clubhouse.impl.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import nu.borjessons.clubhouse.impl.data.User;

import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
@ToString
public final class UserDTO {

  private final String clubId;
  private final String userId;
  private final String firstName;
  private final String lastName;
  private final String dateOfBirth;
  private final String email;
  private final Set<String> childrenIds;
  private final Set<String> parentIds;
  private final Set<String> roles;

  public static UserDTO create(User user, String clubId) {
    return UserDTO.builder()
        .clubId(clubId)
        .userId(user.getUserId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .dateOfBirth(user.getDateOfBirth().toString())
        .roles(user.getRolesForClub(clubId))
        .childrenIds(user.getChildren().stream().map(User::getUserId).collect(Collectors.toSet()))
        .parentIds(user.getParents().stream().map(User::getUserId).collect(Collectors.toSet()))
        .build();
  }
}
