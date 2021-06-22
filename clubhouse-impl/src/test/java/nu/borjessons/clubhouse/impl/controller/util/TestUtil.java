package nu.borjessons.clubhouse.impl.controller.util;

import java.time.LocalDate;
import java.util.Set;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestUtil {
  public static final String CHILD_1_ID = "child1";
  public static final String CLUB_1_ID = "club1";
  public static final String OWNER_1_ID = "owner1";
  public static final String PARENT_1_ID = "parent1";
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
    Club club = new Club("Robins Sports Club", Club.Type.SPORT, CLUB_1_ID);

    User owner = new User(OWNER_1_ID);
    owner.setFirstName("Robin");
    owner.setLastName("Börjesson");
    owner.setEmail("robin.b@outlook.com");
    owner.setDateOfBirth(LocalDate.of(1982, 2, 15));
    owner.setId(1);

    User user1 = new User(USER_1);
    user1.setFirstName("User1");
    user1.setLastName("User1son");
    user1.setEmail("user1@outlook.com");
    user1.setDateOfBirth(LocalDate.of(1982, 2, 15));
    user1.setId(2);

    User dad = new User(PARENT_1_ID);
    dad.setEmail("dad@outlook.com");
    dad.setFirstName("Dad");
    dad.setLastName("Familyson");
    dad.setDateOfBirth(LocalDate.of(1982, 2, 15));
    dad.setId(3);

    User child = new User(CHILD_1_ID);
    child.setFirstName("Child1");
    child.setLastName("Familyson");
    child.setEmail("child1@outlook.com");
    child.setDateOfBirth(LocalDate.of(2012, 10, 24));
    child.setManagedAccount(true);
    child.addParent(dad);

    Team team = new Team(TEAM_1_ID);
    team.addMember(user1);
    team.setId(1);
    team.setName("Team 1");
    team.setMinAge(5);
    team.setMaxAge(90);
    team.setLeaders(Set.of(owner));

    club.addTeam(team);

    return club;
  }
}
