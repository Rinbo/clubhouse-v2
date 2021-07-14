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
  @Test
  void getClubUser() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> clubUsers = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken);
      final ClubUserDTO papa = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.POPS_EMAIL);
      final String papaUserId = papa.getUserId();

      Assertions.assertEquals(papa, UserUtil.getUser(EmbeddedDataLoader.CLUB1_ID, ownerToken, papaUserId));

      final String papaToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      try {
        UserUtil.getUser(EmbeddedDataLoader.CLUB1_ID, papaToken, papaUserId);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
    }
  }

  @Test
  void getClubUsersByAgeRange() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> children = UserUtil.getClubUsersByAge(EmbeddedDataLoader.CLUB1_ID, ownerToken, 5, 12);
      Assertions.assertEquals(2, children.size());

      final List<ClubUserDTO> grownUps = UserUtil.getClubUsersByAge(EmbeddedDataLoader.CLUB1_ID, ownerToken, 20, 100);
      Assertions.assertEquals(3, grownUps.size());
    }
  }

  @Test
  void removeClubUser() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      final List<ClubUserDTO> clubUsers = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken);
      final ClubUserDTO papa = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.POPS_EMAIL);
      final ClubUserDTO mommy = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.MOMMY_EMAIL);

      UserUtil.removeClubUser(EmbeddedDataLoader.CLUB1_ID, ownerToken, papa.getUserId());
      Assertions.assertEquals(4, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken).size());

      UserUtil.removeClubUser(EmbeddedDataLoader.CLUB1_ID, ownerToken, mommy.getUserId());
      Assertions.assertEquals(3, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken).size());
    }
  }
}
