package nu.borjessons.clubhouse.impl;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import nu.borjessons.clubhouse.impl.config.TestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class ClubJuggleIntegrationTests {

  private static String clubId1;
  private static String clubId2;
  private static String user1Id;
  private static String user2Id;
  private static String user1AuthToken;
  private static String user2AuthToken;
  private static String admin1AuthToken;
  private static String team1Id;
  private static List<String> childrenIds;

  @LocalServerPort private int port;

  @BeforeEach
  void setUp() throws Exception {
    RestAssured.baseURI = TestConfiguration.HOST;
    RestAssured.port = port;
  }

  @Test
  void aa_createPreReqs() {
    clubId1 = TestSetupFactory.registerClub("Borjessons IK", "admin1@ex.com");
    clubId2 = TestSetupFactory.registerClub("Judo JK", "admin2@ex.com");
    assertNotNull(clubId1);
    assertNotNull(clubId2);

    user1Id = TestSetupFactory.registerNormalUserWithChildren(clubId1, "robin@ex.com", 2);
    user2Id = TestSetupFactory.registerNormalUserWithChildren(clubId1, "maria@ex.com", 0);
    assertNotNull(user1Id);
    assertNotNull(user2Id);

    user1AuthToken = TestSetupFactory.loginUser("robin@ex.com");
    user2AuthToken = TestSetupFactory.loginUser("maria@ex.com");
    assertNotNull(user1AuthToken);
    assertNotNull(user2AuthToken);

    admin1AuthToken = TestSetupFactory.loginUser("admin1@ex.com");
    assertNotNull(admin1AuthToken);

    team1Id = TestSetupFactory.createTeam(admin1AuthToken, "Juniors");
    assertNotNull(team1Id);
  }

  @Test
  void ab_user1GetsChildrenIds() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", user1AuthToken)
            .when()
            .get("/users/principal")
            .then()
            .statusCode(200)
            .extract()
            .response();

    childrenIds = response.jsonPath().getList("childrenIds", String.class);
    assertNotNull(childrenIds);
  }

  @Test
  void ac_adminAddsChildrentoSecondUser() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", admin1AuthToken)
            .pathParam("userId", user2Id)
            .body(childrenIds)
            .when()
            .post("/users/children/{userId}")
            .then()
            .statusCode(200)
            .extract()
            .response();

    List<String> p2ChildrenIds = response.jsonPath().getList("childrenIds", String.class);
    assertEquals(childrenIds, p2ChildrenIds);
  }

  @Test
  void ad_user1AddsAChildToATeam() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", user1AuthToken)
            .params("childId", childrenIds.get(0), "teamId", team1Id)
            .when()
            .put("/teams/principal/child/add")
            .then()
            .statusCode(200)
            .extract()
            .response();

    String teamId = response.jsonPath().getString("teamId");
    assertEquals(team1Id, teamId);
  }

  @Test
  void ae_user1LeavesClub1() {
    given()
        .contentType(TestConfiguration.APPLICATION_JSON)
        .accept(TestConfiguration.APPLICATION_JSON)
        .header("Authorization", user1AuthToken)
        .when()
        .put("/users/principal/leave-club")
        .then()
        .statusCode(200);
  }

  @Test
  void af_user2ChecksIfChildrenAreStillInTeam() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", user2AuthToken)
            .pathParam("teamId", team1Id)
            .when()
            .get("/teams/principal/{teamId}")
            .then()
            .log()
            .body()
            .statusCode(200)
            .extract()
            .response();

    List<Map<String, String>> members = response.jsonPath().getList("members");
    String childId = members.get(0).get("userId");
    assertEquals(childrenIds.get(0), childId);
  }

  @Test
  void ag_user2LeavesClub1() {
    given()
        .contentType(TestConfiguration.APPLICATION_JSON)
        .accept(TestConfiguration.APPLICATION_JSON)
        .header("Authorization", user2AuthToken)
        .when()
        .put("/users/principal/leave-club")
        .then()
        .statusCode(200);
  }

  @Test
  void ah_adminAssuresSoleTeamIsEmpty() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", admin1AuthToken)
            .pathParam("teamId", team1Id)
            .when()
            .get("/teams/principal/{teamId}")
            .then()
            .log()
            .body()
            .statusCode(200)
            .extract()
            .response();

    List<Map<String, String>> members = response.jsonPath().getList("members");
    assertTrue(members.isEmpty());
  }
}
