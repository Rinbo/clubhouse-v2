package nu.borjessons.clubhouse.integration.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.ClubColorRecord;
import nu.borjessons.clubhouse.impl.util.dev.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.ClubUtil;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.RegistrationUtil;
import nu.borjessons.clubhouse.integration.tests.util.RestUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ClubIntegrationTest {
  private static final ClubColorRecord CLUB_COLOR_RECORD = new ClubColorRecord("#123456", "#AACCFF");

  private static void validateAgainstTestClub(ClubRecord ownerClubRecord) {
    Assertions.assertEquals(EmbeddedDataLoader.CLUB_ID, ownerClubRecord.clubId());
    Assertions.assertEquals("Fritiof Sports", ownerClubRecord.name());
    Assertions.assertEquals("fritiof-sports", ownerClubRecord.path());
    Assertions.assertEquals("SPORT", ownerClubRecord.type().toString());
  }

  @Test
  void adminGetsClubTest() {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      ClubRecord ownerClubRecord = ClubUtil.getClub(EmbeddedDataLoader.CLUB_ID, ownerToken);
      validateAgainstTestClub(ownerClubRecord);
    }
  }

  @Test
  void ownerDeletesClubTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      String userToken = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDto fenix = RegistrationUtil.registerClub(ClubUtil.createClubModel("Fenix"));
      String fenixToken = UserUtil.loginUser(fenix.getEmail(), EmbeddedDataLoader.DEFAULT_PASSWORD);

      Assertions.assertEquals(2, ClubUtil.getClubs().size());
      RestUtil.verifyForbiddenAccess(() -> ClubUtil.deleteClub(EmbeddedDataLoader.CLUB_ID, userToken));
      RestUtil.verifyForbiddenAccess(() -> ClubUtil.deleteClub(EmbeddedDataLoader.CLUB_ID, fenixToken));

      ClubUtil.deleteClub(EmbeddedDataLoader.CLUB_ID, ownerToken);
      Assertions.assertTrue(UserUtil.getPrincipalClubUsers(ownerToken).isEmpty());
      Assertions.assertEquals(1, ClubUtil.getClubs().size());
    }
  }

  @Test
  void updateClubColorIntegrationTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      ClubRecord clubRecord = ClubUtil.updateClubColor(EmbeddedDataLoader.CLUB_ID, token, CLUB_COLOR_RECORD);
      Assertions.assertEquals(CLUB_COLOR_RECORD.primaryColor(), clubRecord.primaryColor());
      Assertions.assertEquals(CLUB_COLOR_RECORD.secondaryColor(), clubRecord.secondaryColor());

      String userToken = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      RestUtil.verifyForbiddenAccess(() -> ClubUtil.updateClubColor(EmbeddedDataLoader.CLUB_ID, userToken, CLUB_COLOR_RECORD));
    }
  }

  @Test
  void userGetsClubTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      List<ClubUserDto> clubUserDtos = ClubUtil.getClubLeaders(EmbeddedDataLoader.CLUB_ID, ownerToken);
      Assertions.assertEquals(3, clubUserDtos.size());
    }
  }
}
