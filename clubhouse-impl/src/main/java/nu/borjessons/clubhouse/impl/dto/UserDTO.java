package nu.borjessons.clubhouse.impl.dto;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import nu.borjessons.clubhouse.impl.data.User;

@Builder
@Getter
@ToString
public final class UserDTO {
  public static UserDTO create(User user) {
    return UserDTO.builder()
        .clubs(user.getClubs().stream().map(ClubDTO::new).collect(Collectors.toSet()))
        .userId(user.getUserId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .dateOfBirth(user.getDateOfBirth().toString())
        .childrenIds(user.getChildren().stream().map(User::getUserId).collect(Collectors.toSet()))
        .parentIds(user.getParents().stream().map(User::getUserId).collect(Collectors.toSet()))
        .addresses(user.getAddresses().stream().map(AddressDTO::new).collect(Collectors.toSet()))
        .build();
  }

  private final Set<AddressDTO> addresses;
  private final Set<String> childrenIds;
  private final Set<ClubDTO> clubs;
  private final String dateOfBirth;
  private final String email;
  private final String firstName;
  private final String lastName;
  private final Set<String> parentIds;
  private final String userId;
}
