package nu.borjessons.clubhouse.impl.dto;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.RoleEntity;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.ImageTokenId;
import nu.borjessons.clubhouse.impl.data.key.UserId;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class ClubUserDto {
  private Set<String> childrenIds;
  private ClubRecord club;
  private String dateOfBirth;
  private String firstName;
  private ImageTokenId imageTokenId;
  private String lastName;
  private boolean managedUser;
  private Set<String> parentIds;
  private Set<Role> roles;
  private boolean showEmail;
  private UserId userId;

  public ClubUserDto(ClubUser clubUser) {
    Objects.requireNonNull(clubUser, "ClubUser must not be null");

    User user = clubUser.getUser();

    childrenIds = user.getChildren().stream().map(User::getUserId).map(UserId::toString).collect(Collectors.toSet());
    club = new ClubRecord(clubUser.getClub());
    dateOfBirth = user.getDateOfBirth().toString();
    firstName = user.getFirstName();
    imageTokenId = user.getImageTokenId().orElse(null);
    lastName = user.getLastName();
    managedUser = user.isManagedAccount();
    parentIds = user.getParents().stream().map(User::getUserId).map(UserId::toString).collect(Collectors.toSet());
    roles = clubUser.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet());
    showEmail = user.isShowEmail();
    userId = user.getUserId();
  }
}
