package nu.borjessons.clubhouse.impl;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import nu.borjessons.clubhouse.impl.config.TestConfiguration;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.impl.controller.model.request.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.integration.util.RequestModels;
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

import java.util.*;

import static io.restassured.RestAssured.given;
import static nu.borjessons.clubhouse.impl.integration.util.RequestModels.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = "test")
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class RegistrationEndpointTests {

  private static String clubId;
  private static String adminAuthToken;
  private static String userAuthToken;
  private static String adminUserId;
  private static String normalUserId;

  @LocalServerPort private int port;

  @BeforeEach
  void setUp() throws Exception {
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
    adminUserId = response.jsonPath().getString("userId");
    assertNotNull(clubId);
    assertNotNull(adminUserId);
  }

  @Test
  void ab_conflictWhenRegisterSameClub() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(clubRegistrationRequest(CLUB_1, ADMIN_USERNAME, ADMIN_NAME))
            .when()
            .post("/register/club")
            .then()
            .statusCode(409)
            .contentType(TestConfiguration.APPLICATION_JSON)
            .extract()
            .response();

    String errorResponse = response.jsonPath().getString("message");
    assertThat(errorResponse)
        .contains("A resource already exists in the database with provided parameters");
  }

  @Test
  void ac_conflictWhenRegisterDifferentClubButSameUser() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(clubRegistrationRequest(CLUB_2, ADMIN_USERNAME, ADMIN_NAME))
            .when()
            .post("/register/club")
            .then()
            .statusCode(409)
            .contentType(TestConfiguration.APPLICATION_JSON)
            .extract()
            .response();

    String errorResponse = response.jsonPath().getString("message");
    assertThat(errorResponse)
        .contains("A resource already exists in the database with provided parameters");
  }

  @Test
  void ba_registerUserWithChildren() {
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
    List<String> childrenIds = response.jsonPath().getList("childrenIds", String.class);
    normalUserId = response.jsonPath().getString("userId");
    assertNotNull(normalUserId);
    assertTrue(roles.contains("PARENT"));
    assertTrue(roles.contains("USER"));
    assertEquals(2, childrenIds.size());
    assertNotNull(email);
    assertEquals(NORMAL_USER_USERNAME, email);
  }

  @Test
  void ca_loginAdminUser() {
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
  void cb_adminGetsUserByUserId() {
    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", adminAuthToken)
            .when()
            .pathParam("userId", normalUserId)
            .get("/users/{userId}")
            .then()
            .statusCode(200)
            .extract()
            .response();

    String email = response.jsonPath().getString("email");
    String firstName = response.jsonPath().getString("firstName");
    String lastName = response.jsonPath().getString("lastName");
    List<String> childrenIds = response.jsonPath().getList("childrenIds", String.class);
    assertNotNull(email);
    assertEquals(NORMAL_USER_USERNAME, email);
    assertEquals(NORMAL_USER1_NAME.split(" ")[0], firstName);
    assertEquals(NORMAL_USER1_NAME.split(" ")[1], lastName);
    assertEquals(2, childrenIds.size());
  }

  @Test
  void da_loginUser() {
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
  void db_accessDeniedWhenloginUserWithWrongPassword() {
    given()
        .contentType(TestConfiguration.APPLICATION_JSON)
        .accept(TestConfiguration.APPLICATION_JSON)
        .body(loginRequest(NORMAL_USER_USERNAME, "WrongPassword"))
        .when()
        .post("/login")
        .then()
        .statusCode(403);
  }

  @Test
  void dc_accessDeniedWhenUserTriesAdminEndpoint() {
    given()
        .contentType(TestConfiguration.APPLICATION_JSON)
        .accept(TestConfiguration.APPLICATION_JSON)
        .header("Authorization", userAuthToken)
        .when()
        .pathParam("userId", adminUserId)
        .get("/users/{userId}")
        .then()
        .statusCode(403);
  }

  @Test
  void dd_userGetsPrincipal() {
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

    String email = response.jsonPath().getString("email");
    String firstName = response.jsonPath().getString("firstName");
    String lastName = response.jsonPath().getString("lastName");
    List<String> childrenIds = response.jsonPath().getList("childrenIds", String.class);
    assertNotNull(email);
    assertEquals(NORMAL_USER_USERNAME, email);
    assertEquals(NORMAL_USER1_NAME.split(" ")[0], firstName);
    assertEquals(NORMAL_USER1_NAME.split(" ")[1], lastName);
    assertEquals(2, childrenIds.size());
  }

  @Test
  void ea_userRegistersMoreChildren() {

    Set<CreateChildRequestModel> childModels = new HashSet<>();
    CreateChildRequestModel childModel1 = new CreateChildRequestModel();
    childModel1.setFirstName("Alva");
    childModel1.setLastName("Börjesson");
    childModel1.setDateOfBirth("2020-08-05");
    CreateChildRequestModel childModel2 = new CreateChildRequestModel();
    childModel2.setFirstName("Torleif");
    childModel2.setLastName("Börjesson");
    childModel2.setDateOfBirth("2020-08-01");
    childModels.add(childModel1);
    childModels.add(childModel2);

    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", userAuthToken)
            .body(childModels)
            .when()
            .post("/register/principal/children")
            .then()
            .statusCode(200)
            .extract()
            .response();

    List<String> childrenList = response.jsonPath().getList("childrenIds");

    assertEquals(4, childrenList.size());
  }

  @Test
  void fa_registerFamily() {

    FamilyRequestModel family = new FamilyRequestModel();
    CreateUserModel mom = new CreateUserModel();
    mom.setFirstName("Maria");
    mom.setLastName("Börjesson");
    mom.setClubId(clubId);
    mom.setDateOfBirth("1984-01-01");
    mom.setEmail("mom@ex.com");
    mom.setPassword(RequestModels.GENERIC_PASSWORD);

    CreateUserModel dad = new CreateUserModel();
    dad.setFirstName("Rolle");
    dad.setLastName("Börjesson");
    dad.setClubId(clubId);
    dad.setDateOfBirth("1982-01-01");
    dad.setEmail("dad@ex.com");
    dad.setPassword(RequestModels.GENERIC_PASSWORD);

    CreateChildRequestModel albin = new CreateChildRequestModel();
    albin.setFirstName("Albin");
    albin.setLastName("Börjesson");
    albin.setDateOfBirth("2015-01-01");

    CreateChildRequestModel sixten = new CreateChildRequestModel();
    sixten.setFirstName("Sixten");
    sixten.setLastName("Börjesson");
    sixten.setDateOfBirth("2012-01-01");

    family.setParents(Arrays.asList(mom, dad));
    family.setChildren(Arrays.asList(albin, sixten));
    family.setClubId(clubId);

    Response response =
        given()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(family)
            .when()
            .post("/register/family")
            .then()
            .statusCode(200)
            .extract()
            .response();

    List<Map<String, Object>> users = response.jsonPath().getList("");
    assertNotNull(users);

    Map<String, Object> user1 = users.get(0);
    assertNotNull(user1);

    @SuppressWarnings("unchecked")
    List<String> childrenIds = (List<String>) user1.get("childrenIds");

    assertNotNull(childrenIds);

    assertEquals(2, childrenIds.size());
    assertEquals(2, users.size());
  }
}
