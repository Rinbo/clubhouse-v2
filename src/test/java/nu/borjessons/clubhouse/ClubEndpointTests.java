package nu.borjessons.clubhouse;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import nu.borjessons.clubhouse.config.TestConfiguration;
import nu.borjessons.clubhouse.controller.model.request.CreateUserModel;
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

import static io.restassured.RestAssured.given;
import static nu.borjessons.clubhouse.integration.util.RequestModels.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class ClubEndpointTests {

  private static String clubId1;
  private static String clubId2;
  private static String userId;
  private static String userAuthToken;
  private static String adminUserId;

  @LocalServerPort private int port;

  @BeforeEach
  void setUp() throws Exception {
    RestAssured.baseURI = TestConfiguration.HOST;
    RestAssured.port = port;
  }

  @Test
  void aa_registerClub1() {
    Response response =
        given()
            .log()
            .all()
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

    clubId1 = response.jsonPath().getString("clubId");
    adminUserId = response.jsonPath().getString("userId");
    assertNotNull(clubId1);
    assertNotNull(adminUserId);
  }

  @Test
  void ab_createSecondClub() {

    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(clubRegistrationRequest("Borjessons IK", "admin2@ex.com", "Admin2 Adminsson"))
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
  void ac_registerNormalUser() {
    CreateUserModel userModel = new CreateUserModel();
    userModel.setFirstName("User");
    userModel.setLastName("Usersson");
    userModel.setClubId(clubId1);
    userModel.setDateOfBirth("1984-04-04");
    userModel.setEmail("user@ex.com");
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

    userId = response.jsonPath().getString("userId");
    assertNotNull(userId);
  }

  @Test
  void ad_loginUser() {
    Response loginResponse =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(loginRequest("user@ex.com", GENERIC_PASSWORD))
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
  void ae_userJoinsClub2() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", userAuthToken)
            .params("clubId", clubId2)
            .when()
            .put("/users/principal/join-club")
            .then()
            .statusCode(200)
            .extract()
            .response();

    String clubId_2 = response.jsonPath().getString("clubId");
    assertNotNull(clubId_2);
    assertEquals(clubId2, clubId_2);
  }

  @Test
  void ba_userGetsActiveClub() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", userAuthToken)
            .when()
            .get("/clubs/principal/active")
            .then()
            .statusCode(200)
            .extract()
            .response();

    String clubId_2 = response.jsonPath().getString("clubId");
    String clubName = response.jsonPath().getString("name");
    String path = response.jsonPath().getString("path");
    assertNotNull(clubId_2);
    assertEquals(clubId2, clubId_2);
    assertEquals("Borjessons IK", clubName);
    assertEquals("borjessons-ik", path);
  }

  @Test
  void ba_userGetsAllHisClubs() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", userAuthToken)
            .when()
            .get("/clubs/principal")
            .then()
            .statusCode(200)
            .extract()
            .response();

    List<Object> clubs = response.jsonPath().getList("");
    assertNotNull(clubs);
    assertEquals(2, clubs.size());
  }

  @Test
  void ca_getAllClubsWithoutCredentials() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .when()
            .get("/clubs")
            .then()
            .statusCode(200)
            .extract()
            .response();

    List<Object> clubs = response.jsonPath().getList("");
    assertNotNull(clubs);
    assertEquals(3, clubs.size());
  }
}
