package nu.borjessons.clubhouse;

import static io.restassured.RestAssured.given;
import static nu.borjessons.clubhouse.integration.util.RequestModels.ADMIN_USER_NAME;
import static nu.borjessons.clubhouse.integration.util.RequestModels.ADMIN_USER_USERNAME;
import static nu.borjessons.clubhouse.integration.util.RequestModels.CLUB1;
import static nu.borjessons.clubhouse.integration.util.RequestModels.clubRegistrationRequest;
import static nu.borjessons.clubhouse.integration.util.RequestModels.loginRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import nu.borjessons.clubhouse.config.TestConfiguration;

@SpringBootTest(webEnvironment =WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
class RegistrationEndpointTests {
	
	private static String clubId;
	private static String authToken;
	
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
				.body(clubRegistrationRequest(CLUB1, ADMIN_USER_USERNAME, ADMIN_USER_NAME))
				.when()
				.post("/register/club")
				.then()
				.statusCode(200)
				.contentType(TestConfiguration.APPLICATION_JSON)
				.extract()
				.response();

		clubId = response.jsonPath().getString("activeClub");
		assertNotNull(clubId);
	}
	
	@Test
	void ba_loginUser() {
		Response loginResponse = given().contentType(TestConfiguration.APPLICATION_JSON)
				.accept(TestConfiguration.APPLICATION_JSON)
				.body(loginRequest(ADMIN_USER_USERNAME))
				.when()
				.post("/login")
				.then()
				.statusCode(200)
				.extract()
				.response();

		authToken = loginResponse.getHeader("Authorization");
		System.out.println(authToken);
		assertNotNull(authToken);
	}

}
