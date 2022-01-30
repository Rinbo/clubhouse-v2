package nu.borjessons.clubhouse.integration.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.integration.tests.util.ClubUtil;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.RestUtil;

class OpenRoutesIntegrationTest {
  @Test
  void getClubs() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      ResponseEntity<ClubRecord[]> response = new RestTemplate().getForEntity(RestUtil.BASE_URL + "/public/clubs", ClubRecord[].class);
      ClubRecord[] body = response.getBody();
      Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
      Assertions.assertNotNull(body);
      Assertions.assertEquals(1, body.length);
    }
  }

  @Test
  void getClubByPathName() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      ClubRecord clubRecord = ClubUtil.getClubByPathName("fritiof-sports");
      Assertions.assertNotNull(clubRecord);
      Assertions.assertEquals("Fritiof Sports", clubRecord.name());
      Assertions.assertEquals(Club.Type.SPORT, clubRecord.type());
    }
  }
}
