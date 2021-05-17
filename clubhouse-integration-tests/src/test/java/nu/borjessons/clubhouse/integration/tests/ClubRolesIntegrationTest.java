package nu.borjessons.clubhouse.integration.tests;

import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

class ClubRolesIntegrationTest {

  @Test
  void integrationTest() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String token = IntegrationTestHelper.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDTO userDTO = IntegrationTestHelper.getSelf(token);
      List<String> roles = IntegrationTestHelper.getRoles(token, userDTO.getClubs().iterator().next());
      roles.sort(String::compareTo);

      List<String> expected = List.of("ADMIN", "LEADER", "OWNER", "USER");
      Assertions.assertEquals(expected, roles);
    }
  }
}
