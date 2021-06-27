package nu.borjessons.clubhouse.integration.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.ClubUtil;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.RegistrationUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class RegistrationIntegrationTest {
  public static final String DAD_EMAIL = "pops@ex.com";
  public static final String PASSWORD = "password";
  public static final String CHILD_NAME = "Generic";

  @Test
  void dadRegistersNewChildTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String token = UserUtil.loginUser(DAD_EMAIL, PASSWORD);
      UserDTO daddy = UserUtil.getSelf(token);

      Assertions.assertNotNull(daddy);
      Assertions.assertEquals(2, daddy.getChildrenIds().size());

      UserDTO daddyWithOneMoreKid = RegistrationUtil.registerChild(EmbeddedDataLoader.CLUB1_ID, CHILD_NAME, daddy.getUserId(),
          token);

      Assertions.assertNotNull(daddyWithOneMoreKid);
      Assertions.assertEquals(3, daddyWithOneMoreKid.getChildrenIds().size());
    }
  }

  @Test
  void adminRegistersNewChildTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, PASSWORD);
      List<UserDTO> users = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, token);
      String daddyId = getUserIdByEmail(users, DAD_EMAIL);

      UserDTO daddyWithOneMoreKid = RegistrationUtil.registerChild(EmbeddedDataLoader.CLUB1_ID, CHILD_NAME, daddyId, token);

      Assertions.assertNotNull(daddyWithOneMoreKid);
      Assertions.assertEquals(3, daddyWithOneMoreKid.getChildrenIds().size());
    }
  }

  @Test
  void registerClubTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String name = "Owner2";
      CreateClubModel createClubModel = ClubUtil.createClubModel(name);
      final UserDTO userDTO = RegistrationUtil.registerClub(createClubModel);
      Assertions.assertEquals(name, userDTO.getFirstName());

      final List<ClubDTO> clubs = ClubUtil.getClubs();
      Assertions.assertTrue(clubs.stream().anyMatch(club -> club.getName().equals(name + " Sports")));
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
