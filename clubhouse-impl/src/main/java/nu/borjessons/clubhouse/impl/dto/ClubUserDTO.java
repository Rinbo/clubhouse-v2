package nu.borjessons.clubhouse.impl.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.RoleEntity;
import nu.borjessons.clubhouse.impl.data.User;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class ClubUserDTO implements Serializable {
  @Serial
  private static final long serialVersionUID = 7264890058314991499L;

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
  private String email;
  private String dateOfBirth;
  private String clubId;
  private String clubName;
  private Set<BaseUserRecord> children;
  private Set<String> parentIds;
  private Set<Role> roles;
  private String userId;

  public ClubUserDTO(ClubUser clubUser) {
    Objects.requireNonNull(clubUser, "ClubUser must no be null");

    firstName = clubUser.getUser().getFirstName();
    lastName = clubUser.getUser().getLastName();
    email = clubUser.getUser().getEmail();
    dateOfBirth = clubUser.getUser().getDateOfBirth().toString();
    clubId = clubUser.getClub().getClubId();
    clubName = clubUser.getClub().getName();
    children = getClubChildren(clubUser);
    parentIds = clubUser.getUser().getParents().stream().map(User::getUserId).collect(Collectors.toSet());
    roles = clubUser.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet());
    userId = clubUser.getUser().getUserId();
  }
}
