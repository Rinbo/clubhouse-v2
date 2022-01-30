package nu.borjessons.clubhouse.integration.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.ClubUtil;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ClubIntegrationTest {
  private static void validateAgainstTestClub(ClubRecord ownerClubRecord) {
    Assertions.assertEquals(EmbeddedDataLoader.CLUB_ID, ownerClubRecord.clubId());
    Assertions.assertEquals("Fritiof Sports", ownerClubRecord.name());
    Assertions.assertEquals("fritiof-sports", ownerClubRecord.path());
    Assertions.assertEquals("SPORT", ownerClubRecord.type().toString());
  }

  @Test
  void adminGetsClubTest() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final ClubRecord ownerClubRecord = ClubUtil.getClub(EmbeddedDataLoader.CLUB_ID, ownerToken);
      validateAgainstTestClub(ownerClubRecord);
    }
  }

  @Test
  void userGetsClubTest() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String userToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final ClubRecord userClubRecord = ClubUtil.getClub(EmbeddedDataLoader.CLUB_ID, userToken);
      validateAgainstTestClub(userClubRecord);
    }
  }
}
