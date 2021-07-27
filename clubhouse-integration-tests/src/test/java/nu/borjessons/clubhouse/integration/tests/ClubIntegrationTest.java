package nu.borjessons.clubhouse.integration.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.ClubUtil;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ClubIntegrationTest {
  private static void validateAgainstTestClub(ClubDTO ownerClubDTO) {
    Assertions.assertEquals(EmbeddedDataLoader.CLUB_ID, ownerClubDTO.getClubId());
    Assertions.assertEquals("Fritiof Sports", ownerClubDTO.getName());
    Assertions.assertEquals("fritiof-sports", ownerClubDTO.getPath());
    Assertions.assertEquals("SPORT", ownerClubDTO.getType().toString());
  }

  @Test
  void adminGetsClubTest() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final ClubDTO ownerClubDTO = ClubUtil.getClub(EmbeddedDataLoader.CLUB_ID, ownerToken);
      validateAgainstTestClub(ownerClubDTO);
    }
  }

  @Test
  void userGetsClubTest() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String userToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final ClubDTO userClubDTO = ClubUtil.getClub(EmbeddedDataLoader.CLUB_ID, userToken);
      validateAgainstTestClub(userClubDTO);
    }
  }
}
