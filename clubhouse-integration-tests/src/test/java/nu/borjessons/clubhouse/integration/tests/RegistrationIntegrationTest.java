package nu.borjessons.clubhouse.integration.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.RegistrationTestHelper;

class RegistrationIntegrationTest {

  public static final String DADDY = "pops@ex.com";
  public static final String PASSWORD = "password";
  public static final String SNORRE = "Snorre";

  @Test
  void daddyRegistersNewChildTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String token = IntegrationTestHelper.loginUser(DADDY, PASSWORD);
      UserDTO daddy = IntegrationTestHelper.getSelf(token);

      Assertions.assertNotNull(daddy);
      Assertions.assertEquals(2, daddy.getChildrenIds().size());

      UserDTO daddyWithOneMoreKid = RegistrationTestHelper.registerChild(EmbeddedDataLoader.CLUB1_ID, SNORRE, daddy.getUserId(),
          token);

      Assertions.assertNotNull(daddyWithOneMoreKid);
      Assertions.assertEquals(3, daddyWithOneMoreKid.getChildrenIds().size());
    }
  }

  @Test
  void adminRegistersNewChildTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String token = IntegrationTestHelper.loginUser(EmbeddedDataLoader.OWNER_EMAIL, PASSWORD);
      List<UserDTO> users = IntegrationTestHelper.getClubUsers(EmbeddedDataLoader.CLUB1_ID, token);
      String daddyId = getUserIdByEmail(users, DADDY);

      UserDTO daddyWithOneMoreKid = RegistrationTestHelper.registerChild(EmbeddedDataLoader.CLUB1_ID, SNORRE, daddyId, token);

      Assertions.assertNotNull(daddyWithOneMoreKid);
      Assertions.assertEquals(3, daddyWithOneMoreKid.getChildrenIds().size());
    }
  }

  private String getUserIdByEmail(List<UserDTO> users, String email) {
    return users
        .stream()
        .filter(user -> user.getEmail().equals(email))
        .findFirst()
        .orElseThrow()
        .getUserId();
  }
}
