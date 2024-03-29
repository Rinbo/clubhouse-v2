package nu.borjessons.clubhouse.integration.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.util.dev.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class TokenIntegrationTest {
  @Test
  void validateAndRevokeTokenTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      final String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(HttpStatus.OK, UserUtil.validateToken(token).getStatusCode());

      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserUtil.revokeToken(EmbeddedDataLoader.CLUB_ID, ownerToken, EmbeddedDataLoader.POPS_EMAIL);

      ResponseEntity<String> response = UserUtil.validateToken(token);
      Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
  }
}
