package nu.borjessons.clubhouse;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import nu.borjessons.clubhouse.config.TestConfiguration;
import nu.borjessons.clubhouse.controller.model.request.CreateTeamModel;

@SpringBootTest(webEnvironment =WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
class TeamEndpointTests {
	
	private static String clubId1;
	private static String clubId2;
	private static String userId;
	private static String userAuthToken;
	private static String admin1AuthToken;
	private static String teamId1;

	@LocalServerPort
	private int port;

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

		userId = TestSetupFactory.registerNormalUserWithChildren(clubId1, "robin@ex.com", 2);
		assertNotNull(userId);
		
		userAuthToken = TestSetupFactory.loginUser("robin@ex.com");
		assertNotNull(userAuthToken);
		
		admin1AuthToken = TestSetupFactory.loginUser("admin1@ex.com");
		assertNotNull(admin1AuthToken);
	}
	
			
	@Test
	void ab_adminCreatesTeam() {
		
		CreateTeamModel teamModel = new CreateTeamModel();
		teamModel.setName("Juniors");
		teamModel.setMinAge(7);
		teamModel.setMaxAge(10);
		
		Response response = given().log().all().contentType(TestConfiguration.APPLICATION_JSON)
				.accept(TestConfiguration.APPLICATION_JSON)
				.header("Authorization", admin1AuthToken)
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
		
		teamId1 = response.jsonPath().getString("teamId");
		String teamName = response.jsonPath().getString("name");
		assertNotNull(teamId1);
		assertEquals(teamModel.getName(), teamName);
	}


}
