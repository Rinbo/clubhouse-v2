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
public final class UserDto {
  public static UserDto create(User user) {
    return UserDto.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .dateOfBirth(user.getDateOfBirth().toString())
        .children(user.getChildren().stream().map(BaseUserRecord::new).collect(Collectors.toSet()))
        .parentIds(user.getParents().stream().map(User::getUserId).collect(Collectors.toSet()))
        .addresses(user.getAddresses().stream().map(AddressDto::new).collect(Collectors.toSet()))
        .managedUser(user.isManagedAccount())
        .build();
  }

  private final Set<AddressDto> addresses;
  private final Set<BaseUserRecord> children;
  private final String dateOfBirth;
  private final String email;
  private final String firstName;
  private final String lastName;
  private final Set<String> parentIds;
  private final String userId;
  private final boolean managedUser;
}
