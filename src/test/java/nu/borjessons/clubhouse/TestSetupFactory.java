package nu.borjessons.clubhouse;

import io.restassured.response.Response;
import nu.borjessons.clubhouse.config.TestConfiguration;
import nu.borjessons.clubhouse.controller.model.request.TeamRequestModel;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static nu.borjessons.clubhouse.integration.util.RequestModels.*;

class TestSetupFactory {

  private static final String UNDERSCORE = "_";
  private static final String CHILD_IDENTIFIER = "CHILD";
  private static final String AT = "@";
  private static final String SPACE = " ";
  private static final String SSON = "sson";

  static String registerClub(String clubName, String username) {

    Response response =
        given()
            .log()
            .all()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(clubRegistrationRequest(clubName, username, generateFullName(username)))
            .when()
            .post("/register/club")
            .then()
            .log()
            .body()
            .statusCode(200)
            .contentType(TestConfiguration.APPLICATION_JSON)
            .extract()
            .response();

    return response.jsonPath().getString("clubId");
  }

  public static String registerNormalUserWithChildren(
      String clubId, String username, int numberOfChildren) {

    String fullName = generateFullName(username);

    List<String> childrenNames = generateChildrenNames(fullName, numberOfChildren);

    Response response =
        given()
            .log()
            .all()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(userWithChildrenRegistrationRequest(clubId, username, fullName, childrenNames))
            .when()
            .post("/register/user")
            .then()
            .log()
            .body()
            .statusCode(200)
            .contentType(TestConfiguration.APPLICATION_JSON)
            .extract()
            .response();

    return response.jsonPath().getString("userId");
  }

  public static String loginUser(String username) {
    Response loginResponse =
        given()
            .log()
            .all()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .body(loginRequest(username, GENERIC_PASSWORD))
            .when()
            .post("/login")
            .then()
            .statusCode(200)
            .extract()
            .response();

    return loginResponse.getHeader("Authorization");
  }

  public static String createTeam(String token, String teamName) {

    TeamRequestModel teamModel = new TeamRequestModel();
    teamModel.setName(teamName);
    teamModel.setMinAge(7);
    teamModel.setMaxAge(10);

    Response response =
        given()
            .log()
            .all()
            .contentType(TestConfiguration.APPLICATION_JSON)
            .accept(TestConfiguration.APPLICATION_JSON)
            .header("Authorization", token)
            .body(teamModel)
            .when()
            .post("/teams")
            .then()
            .log()
            .body()
            .statusCode(200)
            .contentType(TestConfiguration.APPLICATION_JSON)
            .extract()
            .response();

    return response.jsonPath().getString("teamId");
  }

  private static String generateFullName(String username) {
    String firstName = username.split(AT)[0];
    String lastName = firstName + SSON;
    return firstName + SPACE + lastName;
  }

  private static List<String> generateChildrenNames(String fullName, int numberOfChildren) {
    String[] nameArr = fullName.split(SPACE);
    List<String> childrenNames = new ArrayList<>();
    for (int i = 1; i <= numberOfChildren; i++) {
      String name = nameArr[0] + UNDERSCORE + CHILD_IDENTIFIER + i + SPACE + nameArr[1];
      childrenNames.add(name);
    }
    return childrenNames;
  }
}
