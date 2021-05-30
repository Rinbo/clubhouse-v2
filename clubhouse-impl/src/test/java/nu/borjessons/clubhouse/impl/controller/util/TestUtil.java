package nu.borjessons.clubhouse.impl.controller.util;

import java.time.LocalDate;
import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubRole;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtil {
  public static final String CLUB_1_ID = "club1";
  public static final String OWNER_1_ID = "owner1";
  public static final String TEAM_1_ID = "team1";
  public static final String USER_1 = "user1";
  public static final Club CLUB1 = createClub();

  public static User getClubUser(String userId) {
    return CLUB1.getUser(userId).orElseThrow();
  }

  public static Team getTeam(String teamId) {
    return CLUB1
        .getTeams()
        .stream()
        .filter(team -> team.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();
  }

  private static Club createClub() {
    Club club = new Club();
    club.setId(1);
    club.setClubId(CLUB_1_ID);
    club.setName("Robins Sports Club");
    club.setType(Club.Type.SPORT);

    User owner = new User();
    owner.setUserId(OWNER_1_ID);
    owner.setFirstName("Robin");
    owner.setLastName("Börjesson");
    owner.setEmail("robin.b@outlook.com");
    owner.setDateOfBirth(LocalDate.of(1982, 2, 15));
    owner.setId(1);

    User user1 = new User();
    user1.setUserId(USER_1);
    user1.setFirstName("User1");
    user1.setLastName("User1son");
    user1.setEmail("user1@outlook.com");
    user1.setDateOfBirth(LocalDate.of(1982, 2, 15));
    user1.setId(1);

    Set<ClubRole.Role> userRoles = Set.of(ClubRole.Role.USER);
    Set<ClubRole.Role> ownerRoles = Set.of(ClubRole.Role.OWNER, ClubRole.Role.ADMIN, ClubRole.Role.USER, ClubRole.Role.LEADER);
    ownerRoles.forEach(role -> new ClubRole(role, owner, club));
    userRoles.forEach(role -> new ClubRole(role, user1, club));

    Team team = new Team();
    team.addMember(user1);
    team.setTeamId(TEAM_1_ID);
    team.setId(1);
    team.setName("Team 1");
    team.setMinAge(5);
    team.setMaxAge(90);
    team.setLeaders(Set.of(owner));

    club.addTeam(team);

    return club;
  }
}
