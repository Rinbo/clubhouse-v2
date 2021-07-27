package nu.borjessons.clubhouse.integration.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.RestUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class OpenRoutesIntegrationTest {
  @Test
  void getClubs() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      ResponseEntity<ClubDTO[]> response = new RestTemplate().getForEntity(RestUtil.BASE_URL + "/public/clubs", ClubDTO[].class);
      ClubDTO[] body = response.getBody();
      Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assertions.assertNotNull(body);
      Assertions.assertEquals(1, body.length);
    }
  }

  @Test
  void getClubByPathName() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      ClubDTO clubDTO = UserUtil.getClubByPathName("fritiof-sports");
      Assertions.assertNotNull(clubDTO);
      Assertions.assertEquals("Fritiof Sports", clubDTO.getName());
      Assertions.assertEquals(Club.Type.SPORT, clubDTO.getType());
    }
  }
}
