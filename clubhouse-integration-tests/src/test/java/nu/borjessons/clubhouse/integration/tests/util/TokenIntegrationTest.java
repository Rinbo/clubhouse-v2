package nu.borjessons.clubhouse.integration.tests.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;

class TokenIntegrationTest {

  @Test
  void validateAndRevokeTokenTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      final String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(HttpStatus.OK, UserUtil.validateToken(token).getStatusCode());

      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserUtil.revokeToken(EmbeddedDataLoader.CLUB_ID, ownerToken, EmbeddedDataLoader.POPS_EMAIL);

      ResponseEntity<String> response = UserUtil.validateToken(token);
      Assertions.assertEquals(HttpStatus.TEMPORARY_REDIRECT, response.getStatusCode());
    }
  }
}
