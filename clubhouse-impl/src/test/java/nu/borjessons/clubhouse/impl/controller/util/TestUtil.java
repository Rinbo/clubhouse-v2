package nu.borjessons.clubhouse.impl.controller.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubUser;
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
    return CLUB1.getClubUser(userId).orElseThrow().getUser();
  }

  public static Team getTeam(String teamId) {
    return CLUB1
        .getTeams()
        .stream()
        .filter(team -> team.getTeamId().equals(teamId))
        .findFirst()
        .orElseThrow();
  }

  public static String getJsonStringFromFile(String filename) throws IOException {
    try (Reader reader = Files.newBufferedReader(Paths.get("/src/test/resources", filename))) {
      JsonNode jsonNode = new ObjectMapper().readTree(reader);
      return jsonNode.toString();
    }
  }

  private static Club createClub() {
    Club club = new Club("Robins Sports Club", Club.Type.SPORT, CLUB_1_ID);

    User owner = new User(OWNER_1_ID);
    owner.setFirstName("Robin");
    owner.setLastName("Börjesson");
    owner.setEmail("robin.b@outlook.com");
    owner.setDateOfBirth(LocalDate.of(1982, 2, 15));
    owner.setId(1);

    ClubUser clubOwner = new ClubUser();
    clubOwner.setUser(owner);
    clubOwner.setClub(club);
    clubOwner.setRoles(Set.of());

    User user1 = new User(USER_1);
    user1.setFirstName("User1");
    user1.setLastName("User1son");
    user1.setEmail("user1@outlook.com");
    user1.setDateOfBirth(LocalDate.of(1982, 2, 15));
    user1.setId(2);

    ClubUser clubUser = new ClubUser();
    clubUser.setUser(user1);
    clubUser.setClub(club);
    clubUser.setRoles(Set.of());

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
    team.addMember(clubUser);
    team.setId(1);
    team.setName("Team 1");
    team.setMinAge(5);
    team.setMaxAge(90);
    team.setLeaders(List.of(clubOwner));

    club.addTeam(team);

    return club;
  }
}
