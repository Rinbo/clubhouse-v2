package nu.borjessons.clubhouse.impl.dto;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.util.Validate;

@Builder
@Getter
@ToString
public final class UserDto {
  public static UserDto create(User user) {
    Validate.notNull(user, "user");

    String firstName = user.getFirstName();
    String lastName = user.getLastName();
    return UserDto.builder()
        .addresses(user.getAddresses().stream().map(AddressDto::new).collect(Collectors.toSet()))
        .children(user.getChildren().stream().map(BaseUserRecord::new).collect(Collectors.toSet()))
        .darkMode(true)
        .dateOfBirth(user.getDateOfBirth().toString())
        .email(user.getEmail())
        .firstName(firstName)
        .imageTokenId(user.getImageTokenId())
        .lastName(lastName)
        .managedUser(user.isManagedAccount())
        .name(String.format("%s %s", firstName, lastName))
        .parentIds(user.getParents().stream().map(User::getUserId).map(UserId::toString).collect(Collectors.toSet()))
        .userId(user.getUserId())
        .build();
  }

  private final Set<AddressDto> addresses;
  private final Set<BaseUserRecord> children;
  private final boolean darkMode;
  private final String dateOfBirth;
  private final String email;
  private final String firstName;
  private final ImageTokenId imageTokenId;
  private final String lastName;
  private final boolean managedUser;
  private final String name;
  private final Set<String> parentIds;
  private final UserId userId;
}
