package nu.borjessons.clubhouse.impl.controller.util;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubRole;
import nu.borjessons.clubhouse.impl.data.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtil {
  public static final String CLUB_1_ID = "abc123";

  public static Club createClub() {
    Club club = new Club();
    club.setId(1);
    club.setClubId(CLUB_1_ID);
    club.setName("Robins Sports Club");
    club.setType(Club.Type.SPORT);

    User user = new User();
    user.setUserId("123456");
    user.setFirstName("Robin");
    user.setLastName("Börjesson");
    user.setEmail("robin.b@outlook.com");
    user.setDateOfBirth(LocalDate.of(1982, 2, 15));
    user.setId(1);

    new ClubRole(ClubRole.Role.OWNER, user, club);

    return club;
  }

  public static User createOwnerUser() {
    return createClub().getUser("123456").orElseThrow();
  }
}
