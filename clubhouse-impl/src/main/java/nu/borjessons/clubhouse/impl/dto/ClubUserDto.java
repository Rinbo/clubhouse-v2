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

  private String firstName;
  private String lastName;
  private Set<Role> roles;
  private String dateOfBirth;
  private String clubId;
  private String clubName;
  private Set<BaseUserRecord> children;
  private Set<String> parentIds;
  private String userId;
  private String email;

  public ClubUserDto(ClubUser clubUser) {
    Objects.requireNonNull(clubUser, "ClubUser must not be null");

    firstName = clubUser.getUser().getFirstName();
    lastName = clubUser.getUser().getLastName();
    roles = clubUser.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet());
    dateOfBirth = clubUser.getUser().getDateOfBirth().toString();
    clubId = clubUser.getClub().getClubId();
    clubName = clubUser.getClub().getName();
    children = getClubChildren(clubUser);
    parentIds = clubUser.getUser().getParents().stream().map(User::getUserId).collect(Collectors.toSet());
    userId = clubUser.getUser().getUserId();
    email = showEmail() ? clubUser.getUser().getEmail() : null;
  }

  private boolean showEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) return false;

    User principal = (User) authentication.getPrincipal();
    if (userId.equals(principal.getUserId())) return true;

    return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
  }
}
