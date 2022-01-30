package nu.borjessons.clubhouse.integration.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class UserIntegrationTest {
  @Test
  void userGetsAnotherUserByEmail() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      BaseUserRecord mom = UserUtil.getUserByEmail(EmbeddedDataLoader.MOMMY_EMAIL, token);

      Assertions.assertEquals("Mamma", mom.firstName());
      Assertions.assertEquals("Börjesson", mom.lastName());
      Assertions.assertEquals("1984-07-25", mom.dateOfBirth());
    }
  }
}
