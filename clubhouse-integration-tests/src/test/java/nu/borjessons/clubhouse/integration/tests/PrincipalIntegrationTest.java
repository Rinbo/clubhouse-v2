package nu.borjessons.clubhouse.integration.tests;

import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
class PrincipalIntegrationTest {

  @Test
  void integrationTest() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String token = IntegrationTestHelper.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      log.info("AUTH TOKEN: {}", token);

      Assertions.assertTrue(token.contains("Bearer"));
    }
  }
}
