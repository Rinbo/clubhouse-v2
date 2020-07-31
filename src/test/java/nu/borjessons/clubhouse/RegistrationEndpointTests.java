package nu.borjessons.clubhouse;

import static io.restassured.RestAssured.given;
import static nu.borjessons.clubhouse.integration.util.RequestModels.ADMIN_USER_NAME;
import static nu.borjessons.clubhouse.integration.util.RequestModels.ADMIN_USER_USERNAME;
import static nu.borjessons.clubhouse.integration.util.RequestModels.CHILD_1_NAME;
import static nu.borjessons.clubhouse.integration.util.RequestModels.CHILD_2_NAME;
import static nu.borjessons.clubhouse.integration.util.RequestModels.CLUB_1;
import static nu.borjessons.clubhouse.integration.util.RequestModels.CLUB_2;
import static nu.borjessons.clubhouse.integration.util.RequestModels.NORMAL_USER1_NAME;
import static nu.borjessons.clubhouse.integration.util.RequestModels.NORMAL_USER_USERNAME;
import static nu.borjessons.clubhouse.integration.util.RequestModels.clubRegistrationRequest;
import static nu.borjessons.clubhouse.integration.util.RequestModels.loginRequest;
import static nu.borjessons.clubhouse.integration.util.RequestModels.userWithChildrenRegistrationRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import nu.borjessons.clubhouse.config.TestConfiguration;

@SpringBootTest(webEnvironment =WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class RegistrationEndpointTests {
	
	private static Long clubId;
	private static String adminAuthToken;
	
	@LocalServerPort
	private int port;

	@BeforeEach
	void setUp() throws Exception {
		
		RestAssured.baseURI = TestConfiguration.HOST;
		RestAssured.port = port;
	}

	@Test
	void aa_registerClub() {
		Response response = given().contentType(TestConfiguration.APPLICATION_JSON)
				.accept(TestConfiguration.APPLICATION_JSON)
				.body(clubRegistrationRequest(CLUB_1, ADMIN_USER_USERNAME, ADMIN_USER_NAME))
				.when()
				.post("/register/club")
				.then()
				.statusCode(200)
				.contentType(TestConfiguration.APPLICATION_JSON)
				.extract()
				.response();

		clubId = Long.valueOf(response.jsonPath().getString("activeClub"));
		assertNotNull(clubId);
	}
	
	@Test
	void ab_conflictWhenRegisterSameClub() {
		Response response = given().contentType(TestConfiguration.APPLICATION_JSON)
				.accept(TestConfiguration.APPLICATION_JSON)
				.body(clubRegistrationRequest(CLUB_1, ADMIN_USER_USERNAME, ADMIN_USER_NAME))
				.when()
				.post("/register/club")
				.then()
				.statusCode(409)
				.contentType(TestConfiguration.APPLICATION_JSON).extract()
				.response();
		
		String errorResponse = response.jsonPath().getString("message");
		assertThat(errorResponse).contains("A resource already exists in the database with provided parameters");
	}
	
	@Test
	void ac_conflictWhenRegisterDifferentClubButSameUser() {
		Response response = given().contentType(TestConfiguration.APPLICATION_JSON)
				.accept(TestConfiguration.APPLICATION_JSON)
				.body(clubRegistrationRequest(CLUB_2, ADMIN_USER_USERNAME, ADMIN_USER_NAME))
				.when()
				.post("/register/club")
				.then()
				.statusCode(409)
				.contentType(TestConfiguration.APPLICATION_JSON).extract()
				.response();
		
		String errorResponse = response.jsonPath().getString("message");
		assertThat(errorResponse).contains("A resource already exists in the database with provided parameters");
	}
	
	@Test
	void ba_registerUserWithChildren() {
		Response response = given().contentType(TestConfiguration.APPLICATION_JSON)
				.accept(TestConfiguration.APPLICATION_JSON)
				.body(userWithChildrenRegistrationRequest(clubId, NORMAL_USER_USERNAME, NORMAL_USER1_NAME, new ArrayList<>(Arrays.asList(CHILD_1_NAME, CHILD_2_NAME))))
				.when()
				.post("/register/user")
				.then()
				.statusCode(200)
				.contentType(TestConfiguration.APPLICATION_JSON)
				.extract()
				.response();

		String email = response.jsonPath().getString("email");
		List<String> roles = response.jsonPath().getList("roles", String.class);
		List<Long> childrenIds  = response.jsonPath().getList("childrenIds", Long.class);
		assertTrue(roles.contains("PARENT"));
		assertTrue(roles.contains("USER"));
		assertEquals(2, childrenIds.size());
		assertNotNull(email);
		assertEquals(NORMAL_USER_USERNAME, email);
	}

	
	
	@Test
	void ba_loginAdminUser() {
		Response loginResponse = given().contentType(TestConfiguration.APPLICATION_JSON)
				.accept(TestConfiguration.APPLICATION_JSON)
				.body(loginRequest(ADMIN_USER_USERNAME))
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
		Response loginResponse = given().contentType(TestConfiguration.APPLICATION_JSON)
				.accept(TestConfiguration.APPLICATION_JSON)
				.body(loginRequest(NORMAL_USER_USERNAME))
				.when()
				.post("/login")
				.then()
				.statusCode(200)
				.extract()
				.response();

		String userAuthToken = loginResponse.getHeader("Authorization");
		assertNotNull(userAuthToken);
	}
	
	@Test
	void bc_accessDeniedWhenloginUserWithWrongPassword() {
		given().contentType(TestConfiguration.APPLICATION_JSON)
				.accept(TestConfiguration.APPLICATION_JSON)
				.body(loginRequest(NORMAL_USER_USERNAME, "WrongPassword"))
				.when()
				.post("/login")
				.then()
				.statusCode(403);
	}
}
