package nu.borjessons.clubhouse.impl.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
public class ClubUserDTO {
  private String firstName;
  private String lastName;
  private String email;
  private String dateOfBirth;
  private String clubId;
  private List<String> childrenIds = new ArrayList<>();
  private List<Role> roles;
  private String userId;

  public ClubUserDTO(ClubUser clubUser) {
    firstName = clubUser.getUser().getFirstName();
    lastName = clubUser.getUser().getLastName();
    email = clubUser.getUser().getEmail();
    dateOfBirth = clubUser.getUser().getDateOfBirth().toString();
    clubId = clubUser.getClub().getClubId();
    childrenIds = clubUser.getUser().getChildren().stream().map(User::getUserId).collect(Collectors.toList());
    roles = clubUser.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toList());
    userId = clubUser.getUser().getUserId();
  }
}
