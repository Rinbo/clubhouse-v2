package nu.borjessons.clubhouse.integration.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.dto.UserDTO2;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
class PrincipalIntegrationTest {

  @Test
  @Disabled("Temporary")
  void integrationTest() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String token = IntegrationTestHelper.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      log.info("AUTH TOKEN: {}", token);

      Assertions.assertTrue(token.contains("Bearer"));
    }
  }

  @Test
  void clubIdTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String token = IntegrationTestHelper.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      log.info("AUTH TOKEN: {}", token);

      UserDTO2 userDTO = IntegrationTestHelper.getSelf(token);
      log.info("UserDTO: {}", userDTO);
      Assertions.assertTrue(token.contains("Bearer"));
    }
  }
}
