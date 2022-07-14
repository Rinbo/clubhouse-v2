package nu.borjessons.clubhouse.impl.dto;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
  private static Set<BaseUserRecord> getClubChildren(ClubUser clubUser) {
    return clubUser
        .getClub()
        .getClubUsers(
            clubUser
                .getUser()
                .getChildren()
                .stream()
                .map(User::getUserId)
                .toList())
        .stream()
        .map(ClubUser::getUser)
        .map(BaseUserRecord::new)
        .collect(Collectors.toSet());
  }

  private Set<BaseUserRecord> children;
  private ClubRecord club;
  private String clubName;
  private String dateOfBirth;
  private String email;
  private String firstName;
  private ImageTokenId imageTokenId;
  private String lastName;
  private boolean managedUser;
  private Set<String> parentIds;
  private Set<Role> roles;
  private String userId;

  public ClubUserDto(ClubUser clubUser) {
    Objects.requireNonNull(clubUser, "ClubUser must not be null");

    User user = clubUser.getUser();
    UserId uuid = user.getUserId();

    children = getClubChildren(clubUser);
    club = new ClubRecord(clubUser.getClub());
    clubName = clubUser.getClub().getName();
    dateOfBirth = user.getDateOfBirth().toString();
    email = showEmail(uuid) ? user.getEmail() : null;
    firstName = user.getFirstName();
    imageTokenId = user.getImageTokenId();
    lastName = user.getLastName();
    managedUser = user.isManagedAccount();
    parentIds = user.getParents().stream().map(User::getUserId).map(UserId::toString).collect(Collectors.toSet());
    roles = clubUser.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet());
    userId = uuid.toString();
  }

  private boolean showEmail(UserId userId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) return false;

    User principal = (User) authentication.getPrincipal();
    if (userId.toString().equals(principal.getUserId().toString())) return true;

    return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
  }
}
