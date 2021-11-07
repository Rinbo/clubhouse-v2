package nu.borjessons.clubhouse.impl.dto;

import java.io.Serial;
import java.io.Serializable;
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

// TODO refactor to record
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class ClubUserDTO implements Serializable {
  @Serial
  private static final long serialVersionUID = 7264890058314991499L;

  private String firstName;
  private String lastName;
  private String email;
  private String dateOfBirth;
  private String clubId;
  private String clubName;
  private Set<BaseUserDTO> childrenIds;
  private Set<String> parentIds;
  private Set<Role> roles;
  private String userId;

  public ClubUserDTO(ClubUser clubUser) {
    firstName = clubUser.getUser().getFirstName();
    lastName = clubUser.getUser().getLastName();
    email = clubUser.getUser().getEmail();
    dateOfBirth = clubUser.getUser().getDateOfBirth().toString();
    clubId = clubUser.getClub().getClubId();
    clubName = clubUser.getClub().getName();
    // TODO this doesn't quite match up. Here we are giving the childrenIds even if we don't know if this user is actually a parent in this club
    // We could validate on role but that would mean doing work in the constructor
    // Or find the ClubUser children for this club if they exist?
    childrenIds = clubUser.getUser().getChildren().stream().map(BaseUserDTO::new).collect(Collectors.toSet());
    parentIds = clubUser.getUser().getParents().stream().map(User::getUserId).collect(Collectors.toSet());
    roles = clubUser.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet());
    userId = clubUser.getUser().getUserId();
  }
}
