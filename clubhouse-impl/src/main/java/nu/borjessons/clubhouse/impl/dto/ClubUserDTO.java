package nu.borjessons.clubhouse.impl.dto;

import java.io.Serializable;
import java.util.HashSet;
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
  private static final long serialVersionUID = 7264890058314991499L;

  private String firstName;
  private String lastName;
  private String email;
  private String dateOfBirth;
  private String clubId;
  private String clubName;
  private Set<String> childrenIds = new HashSet<>();
  private Set<Role> roles;
  private String userId;

  public ClubUserDTO(ClubUser clubUser) {
    firstName = clubUser.getUser().getFirstName();
    lastName = clubUser.getUser().getLastName();
    email = clubUser.getUser().getEmail();
    dateOfBirth = clubUser.getUser().getDateOfBirth().toString();
    clubId = clubUser.getClub().getClubId();
    clubName = clubUser.getClub().getName();
    childrenIds = clubUser.getUser().getChildren().stream().map(User::getUserId).collect(Collectors.toSet());
    roles = clubUser.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet());
    userId = clubUser.getUser().getUserId();
  }
}
