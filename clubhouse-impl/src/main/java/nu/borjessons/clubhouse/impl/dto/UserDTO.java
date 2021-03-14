package nu.borjessons.clubhouse.impl.dto;

import lombok.Getter;
import lombok.Setter;
import nu.borjessons.clubhouse.impl.data.User;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDTO extends BaseUserDTO implements Serializable {

  private static final long serialVersionUID = 1L;
  private String email;
  private Set<String> childrenIds = new HashSet<>();
  private Set<String> parentIds = new HashSet<>();
  private Set<String> roles = new HashSet<>();
  private String clubId;

  public UserDTO(User user, String clubId) {
    super(user);
    this.clubId = clubId;
    email = user.getEmail();
    roles = user.getRolesForClub(clubId);
    childrenIds = user.getChildren().stream().map(User::getUserId).collect(Collectors.toSet());
    parentIds = user.getParents().stream().map(User::getUserId).collect(Collectors.toSet());
  }
}
