package nu.borjessons.clubhouse.integration.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubDto;
import nu.borjessons.clubhouse.integration.tests.util.ClubUtil;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.RestUtil;

class OpenRoutesIntegrationTest {
  @Test
  void getClubs() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      ResponseEntity<ClubDto[]> response = new RestTemplate().getForEntity(RestUtil.BASE_URL + "/public/clubs", ClubDto[].class);
      ClubDto[] body = response.getBody();
      Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assertions.assertNotNull(body);
      Assertions.assertEquals(1, body.length);
    }
  }

  @Test
  void getClubByPathName() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      ClubDto clubDTO = ClubUtil.getClubByPathName("fritiof-sports");
      Assertions.assertNotNull(clubDTO);
      Assertions.assertEquals("Fritiof Sports", clubDTO.name());
      Assertions.assertEquals(Club.Type.SPORT, clubDTO.type());
    }
  }
}
