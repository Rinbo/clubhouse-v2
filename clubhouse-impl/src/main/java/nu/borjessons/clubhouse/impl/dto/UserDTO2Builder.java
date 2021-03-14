package nu.borjessons.clubhouse.impl.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nu.borjessons.clubhouse.impl.data.User;

import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserDTO2Builder {

  public static UserDTO2 build(User user, String clubId) {
    return UserDTO2.builder()
        .clubId(user.getClubs().stream().filter(club -> club.getClubId().equals(clubId)).findFirst().orElseThrow().getClubId())
        .userId(user.getUserId())
        .email(user.getEmail())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .dateOfBirth(user.getDateOfBirth().toString())
        .roles(user.getRolesForClub(clubId))
        .childrenIds(user.getChildren().stream().map(User::getUserId).collect(Collectors.toSet()))
        .parentIds(user.getParents().stream().map(User::getUserId).collect(Collectors.toSet()))
        .build();
  }
}
