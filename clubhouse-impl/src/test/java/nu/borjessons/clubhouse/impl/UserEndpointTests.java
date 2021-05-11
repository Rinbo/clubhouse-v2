package nu.borjessons.clubhouse.impl;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import nu.borjessons.clubhouse.impl.config.TestConfiguration;
import nu.borjessons.clubhouse.impl.controller.model.request.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.impl.controller.model.request.UpdateUserModel;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubRole;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static nu.borjessons.clubhouse.impl.integration.util.RequestModels.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class UserEndpointTests {

  private static String clubId;
  private static String clubId2;
  private static String userId;
  private static String adminAuthToken;
  private static String userAuthToken;
  private static String parent2AuthToken;
  private static String parent2UserId;
  private static List<String> childrenIds;

  @LocalServerPort
  private int port;

  @BeforeEach
  void setUp() {
    RestAssured.baseURI = TestConfiguration.HOST;
    RestAssured.port = port;
  }

  @Test
  void aa_registerClub() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(clubRegistrationRequest(CLUB_1, ADMIN_USERNAME, ADMIN_NAME))
            .when()
            .post("/register/club")
            .then()
            .statusCode(200)
            .contentType(TestConfiguration.APPLICATION_JSON)
            .extract()
            .response();

    clubId = response.jsonPath().getString("clubId");
    String adminUserId = response.jsonPath().getString("userId");
    assertNotNull(clubId);
    assertNotNull(adminUserId);
  }

  @Test
  void ab_registerUserWithChildren() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(
                userWithChildrenRegistrationRequest(
                    clubId,
                    NORMAL_USER_USERNAME,
                    NORMAL_USER1_NAME,
                    new ArrayList<>(Arrays.asList(CHILD_1_NAME, CHILD_2_NAME))))
            .when()
            .post("/register/user")
            .then()
            .statusCode(200)
            .contentType(TestConfiguration.APPLICATION_JSON)
            .extract()
            .response();

    String email = response.jsonPath().getString("email");
    List<String> roles = response.jsonPath().getList("roles", String.class);
    childrenIds = response.jsonPath().getList("childrenIds", String.class);
    String normalUserId = response.jsonPath().getString("userId");
    assertNotNull(normalUserId);
    assertTrue(roles.contains("PARENT"));
    assertTrue(roles.contains("USER"));
    assertEquals(2, childrenIds.size());
    assertNotNull(email);
    assertEquals(NORMAL_USER_USERNAME, email);
  }

  @Test
  void ba_loginAdminUser() {
    Response loginResponse =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(loginRequest(ADMIN_USERNAME))
            .when()
            .post("/login")
            .then()
            .statusCode(200)
            .extract()
            .response();

    adminAuthToken = loginResponse.getHeader("Authorization");
    assertNotNull(adminAuthToken);
  }

  @Test
  void bb_loginUser() {
    Response loginResponse =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(loginRequest(NORMAL_USER_USERNAME))
            .when()
            .post("/login")
            .then()
            .statusCode(200)
            .extract()
            .response();

    userAuthToken = loginResponse.getHeader("Authorization");
    assertNotNull(userAuthToken);
  }

  @Test
  void bc_userGetsPrincipal() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", userAuthToken)
            .when()
            .get("/users/principal")
            .then()
            .statusCode(200)
            .extract()
            .response();

    userId = response.jsonPath().getString("userId");
    String email = response.jsonPath().getString("email");
    String firstName = response.jsonPath().getString("firstName");
    String lastName = response.jsonPath().getString("lastName");
    List<String> childrenList = response.jsonPath().getList("childrenIds", String.class);
    assertNotNull(userId);
    assertNotNull(email);
    assertEquals(NORMAL_USER_USERNAME, email);
    assertEquals(NORMAL_USER1_NAME.split(" ")[0], firstName);
    assertEquals(NORMAL_USER1_NAME.split(" ")[1], lastName);
    assertEquals(2, childrenList.size());
  }

  @Test
  void ca_userEditsHimself() {

    UpdateUserModel updateUserModel = new UpdateUserModel();
    updateUserModel.setFirstName("Ronny");
    updateUserModel.setLastName("Rolig");
    updateUserModel.setDateOfBirth("2000-01-01");

    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", userAuthToken)
            .body(updateUserModel)
            .when()
            .put("/users/principal")
            .then()
            .statusCode(200)
            .extract()
            .response();

    String id = response.jsonPath().getString("userId");
    String firstName = response.jsonPath().getString("firstName");
    String lastName = response.jsonPath().getString("lastName");
    String dateOfBirth = response.jsonPath().getString("dateOfBirth");

    assertEquals(userId, id);
    assertEquals(updateUserModel.getFirstName(), firstName);
    assertEquals(updateUserModel.getLastName(), lastName);
    assertEquals(updateUserModel.getDateOfBirth(), dateOfBirth);
  }

  @Test
  void cb_adminEditsUser() {

    AdminUpdateUserModel updateUserModel = new AdminUpdateUserModel();
    updateUserModel.setFirstName("User");
    updateUserModel.setLastName("Coolson");
    updateUserModel.setDateOfBirth("1999-12-31");
    updateUserModel.setRoles(Set.of(ClubRole.Role.USER, ClubRole.Role.PARENT));

    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", adminAuthToken)
            .pathParam("userId", userId)
            .body(updateUserModel)
            .when()
            .put("/users/{userId}")
            .then()
            .statusCode(200)
            .extract()
            .response();

    String id = response.jsonPath().getString("userId");
    String firstName = response.jsonPath().getString("firstName");
    String lastName = response.jsonPath().getString("lastName");
    String dateOfBirth = response.jsonPath().getString("dateOfBirth");
    List<String> roles = response.jsonPath().getList("roles");

    assertEquals(userId, id);
    assertEquals(2, roles.size());
    assertEquals(updateUserModel.getFirstName(), firstName);
    assertEquals(updateUserModel.getLastName(), lastName);
    assertEquals(updateUserModel.getDateOfBirth(), dateOfBirth);
  }

  @Test
  void da_registerAnotherUser() {
    CreateUserModel userModel = new CreateUserModel();
    userModel.setFirstName("Parent2");
    userModel.setLastName("Lastname");
    userModel.setClubId(clubId);
    userModel.setDateOfBirth("1984-04-04");
    userModel.setEmail("parent2@ex.com");
    userModel.setPassword(GENERIC_PASSWORD);

    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(userModel)
            .when()
            .post("/register/user")
            .then()
            .statusCode(200)
            .contentType(TestConfiguration.APPLICATION_JSON)
            .extract()
            .response();

    parent2UserId = response.jsonPath().getString("userId");
    assertNotNull(parent2UserId);
  }

  @Test
  void db_adminAddsChildrenToUser2() {

    Set<String> childrenSet = new HashSet<>(childrenIds);

    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", adminAuthToken)
            .pathParam("userId", parent2UserId)
            .body(childrenSet)
            .when()
            .post("/users/children/{userId}")
            .then()
            .statusCode(200)
            .extract()
            .response();

    String id = response.jsonPath().getString("userId");
    List<String> childrenList = response.jsonPath().getList("childrenIds");
    List<String> roles = response.jsonPath().getList("roles");

    assertEquals(parent2UserId, id);
    assertEquals(childrenIds, childrenList);
    assertTrue(roles.contains("PARENT"));
  }

  @Test
  void dc_adminRemovesChildrenFromUser2() {

    Set<String> childrenSet = new HashSet<>();

    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", adminAuthToken)
            .pathParam("userId", parent2UserId)
            .body(childrenSet)
            .when()
            .post("/users/children/{userId}")
            .then()
            .statusCode(200)
            .extract()
            .response();

    String id = response.jsonPath().getString("userId");
    List<String> childrenList = response.jsonPath().getList("childrenIds");
    List<String> roles = response.jsonPath().getList("roles");

    assertEquals(parent2UserId, id);
    assertEquals(0, childrenList.size());
    assertFalse(roles.contains("PARENT"));
  }

  @Test
  void dd_adminAssertsChildrenAreStillPresent() {
    String childId = childrenIds.get(0);

    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", adminAuthToken)
            .when()
            .pathParam("childId", childId)
            .get("/users/{childId}")
            .then()
            .statusCode(200)
            .extract()
            .response();

    String userId = response.jsonPath().getString("userId");

    assertNotNull(userId);
    assertEquals(childId, userId);
  }

  @Test
  void ea_200WhenfirstUserDeletesHimself() {
    given()
        .contentType(TestConfiguration.APPLICATION_JSON)
        .accept(TestConfiguration.APPLICATION_JSON)
        .header("Authorization", userAuthToken)
        .when()
        .delete("/users/principal")
        .then()
        .statusCode(200);
  }

  @Test
  void eb_adminValidatesOrphanedChildrenAreAlsoDeleted() {

    String childId = childrenIds.get(0);

    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", adminAuthToken)
            .when()
            .pathParam("childId", childId)
            .get("/users/{childId}")
            .then()
            .statusCode(404)
            .extract()
            .response();

    String errorResponse = response.jsonPath().getString("message");

    assertEquals(
        "No value present",
        errorResponse);
  }

  @Test
  void fa_createSecondClub() {

    CreateUserModel userModel = new CreateUserModel();
    userModel.setFirstName("Admin2");
    userModel.setLastName("Admin2sson");
    userModel.setClubId("dummy");
    userModel.setDateOfBirth("1984-04-04");
    userModel.setEmail("admin2@ex.com");
    userModel.setPassword(GENERIC_PASSWORD);

    CreateClubModel clubModel = new CreateClubModel("Klubbers IK", Club.Type.SPORT, userModel);

    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(clubModel)
            .when()
            .post("/register/club")
            .then()
            .statusCode(200)
            .contentType(TestConfiguration.APPLICATION_JSON)
            .extract()
            .response();

    clubId2 = response.jsonPath().getString("clubId");
    assertNotNull(clubId2);
  }

  @Test
  void fb_loginUser2() {
    Response loginResponse =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(loginRequest("parent2@ex.com", GENERIC_PASSWORD))
            .when()
            .post("/login")
            .then()
            .statusCode(200)
            .extract()
            .response();

    parent2AuthToken = loginResponse.getHeader("Authorization");
    assertNotNull(userAuthToken);
  }

  @Test
  void fc_user2JoinsClub2() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", parent2AuthToken)
            .params("clubId", clubId2)
            .when()
            .put("/users/principal/join-club")
            .then()
            .statusCode(200)
            .extract()
            .response();

    String clubId_2 = response.jsonPath().getString("clubId");
    assertEquals(clubId2, clubId_2);
  }

  @Test
  void fd_user2SwitchesToClub1() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", parent2AuthToken)
            .params("clubId", clubId)
            .when()
            .put("/users/principal/switch-club")
            .then()
            .statusCode(200)
            .extract()
            .response();

    String clubId_1 = response.jsonPath().getString("clubId");
    assertEquals(clubId, clubId_1);
  }

  @Test
  void fe_user2LeavesClub1() {
    given()
        .contentType(TestConfiguration.APPLICATION_JSON)
        .accept(TestConfiguration.APPLICATION_JSON)
        .header("Authorization", parent2AuthToken)
        .when()
        .put("/users/principal/leave-club")
        .then()
        .statusCode(200);
  }

  @Test
  void ff_403WhenUser2GetsPrincipal() {
    given()
        .contentType(TestConfiguration.APPLICATION_JSON)
        .accept(TestConfiguration.APPLICATION_JSON)
        .header("Authorization", parent2AuthToken)
        .when()
        .get("/users/principal")
        .then()
        .statusCode(403);
  }

  @Test
  void fg_user2SwitchesToClub2() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", parent2AuthToken)
            .params("clubId", clubId2)
            .when()
            .put("/users/principal/switch-club")
            .then()
            .statusCode(200)
            .extract()
            .response();

    String clubId_2 = response.jsonPath().getString("clubId");
    assertEquals(clubId2, clubId_2);
  }

  @Test
  void fh_200WhenUser2GetsPrincipal() {
    given()
        .contentType(TestConfiguration.APPLICATION_JSON)
        .accept(TestConfiguration.APPLICATION_JSON)
        .header("Authorization", parent2AuthToken)
        .when()
        .get("/users/principal")
        .then()
        .statusCode(200);
  }
}
