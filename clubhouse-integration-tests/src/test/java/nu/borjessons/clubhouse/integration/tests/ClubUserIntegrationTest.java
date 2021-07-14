package nu.borjessons.clubhouse.integration.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ClubUserIntegrationTest {

  public static final String POPS_EMAIL = "pops@ex.com";

  @Test
  void getClubUser() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> clubUsers = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken);
      final ClubUserDTO papa = UserUtil.getUserIdByEmail(clubUsers, POPS_EMAIL);

      Assertions.assertEquals(papa, UserUtil.getUser(EmbeddedDataLoader.CLUB1_ID, ownerToken, papa.getUserId()));

      final String papaToken = UserUtil.loginUser(POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      try {
        UserUtil.getUserIdByEmail(clubUsers, POPS_EMAIL);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
    }
  }
}
