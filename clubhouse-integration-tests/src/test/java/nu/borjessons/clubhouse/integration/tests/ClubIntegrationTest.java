package nu.borjessons.clubhouse.integration.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import nu.borjessons.clubhouse.impl.dto.ClubDto;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.ClubUtil;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ClubIntegrationTest {
  private static void validateAgainstTestClub(ClubDto ownerClubDto) {
    Assertions.assertEquals(EmbeddedDataLoader.CLUB_ID, ownerClubDto.clubId());
    Assertions.assertEquals("Fritiof Sports", ownerClubDto.name());
    Assertions.assertEquals("fritiof-sports", ownerClubDto.path());
    Assertions.assertEquals("SPORT", ownerClubDto.type().toString());
  }

  @Test
  void adminGetsClubTest() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final ClubDto ownerClubDto = ClubUtil.getClub(EmbeddedDataLoader.CLUB_ID, ownerToken);
      validateAgainstTestClub(ownerClubDto);
    }
  }

  @Test
  void userGetsClubTest() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String userToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final ClubDto userClubDto = ClubUtil.getClub(EmbeddedDataLoader.CLUB_ID, userToken);
      validateAgainstTestClub(userClubDto);
    }
  }
}
