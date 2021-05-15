package nu.borjessons.clubhouse.integration.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

class ClubRolesIntegrationTest {

  @Test
  void integrationTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      // TODO change this so that userDTO contains a list of all the users clubIds
      String token = IntegrationTestHelper.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      List<ClubDTO> clubs = IntegrationTestHelper.getClubs();
      List<String> roles = IntegrationTestHelper.getRoles(token, clubs.iterator().next());
      List<String> expected = List.of("ADMIN", "OWNER", "USER", "LEADER");
      expected.forEach(role -> Assertions.assertTrue(roles.contains(role)));
    }
  }
}
