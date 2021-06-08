package nu.borjessons.clubhouse.integration.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.impl.controller.model.request.UpdateUserModel;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;

class PrincipalIntegrationTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(PrincipalIntegrationTest.class);
  private static final String OWNER_2_EMAIL = "owner2@ex.com";

  private static CreateClubModel getCreateClubModel() {
    CreateClubModel createClubModel = new CreateClubModel();

    CreateUserModel createUserModel = new CreateUserModel();
    createUserModel.setClubId("Dummy");
    createUserModel.setFirstName("Owner2");
    createUserModel.setLastName("Lastname");
    createUserModel.setEmail(OWNER_2_EMAIL);
    createUserModel.setDateOfBirth("1982-03-16");
    createUserModel.setPassword(EmbeddedDataLoader.DEFAULT_PASSWORD);

    createClubModel.setName("Cool Club");
    createClubModel.setType(Club.Type.MISC);
    createClubModel.setOwner(createUserModel);

    return createClubModel;
  }

  @Test
  void getClubUsers() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String token = IntegrationTestHelper.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      final UserDTO self = IntegrationTestHelper.getSelf(token);
      final List<UserDTO> users = IntegrationTestHelper.getClubUsers(self.getClubs().stream().findFirst().orElseThrow().getClubId(), token);
      Assertions.assertNotNull(users);
      Assertions.assertEquals(5, users.size());
    }
  }

  @Test
  void registerClubAndSwitchTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      // Fetch clubId for default club
      UserDTO userDTO1 = IntegrationTestHelper.getSelf(IntegrationTestHelper.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD));

      Assertions.assertNotNull(userDTO1);
      // Create Second user and club
      CreateClubModel createClubModel = getCreateClubModel();
      UserDTO userDTO2 = IntegrationTestHelper.registerClub(createClubModel);

      Assertions.assertNotNull(userDTO2);

      // Second user joins first club
      /*UserDTO userDTO3 = IntegrationTestHelper.joinClub(
          IntegrationTestHelper.loginUser(OWNER_2_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD), userDTO1.getClubId());

      // Assert that active club gets set to first club when joining
      UserDTO userDTO4 = IntegrationTestHelper.getSelf(IntegrationTestHelper.loginUser(OWNER_2_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD));
      Assertions.assertEquals(userDTO3.getClubId(), userDTO4.getClubId());

      // Login with header set to second club and verify that second club is not the active club for second user
      UserDTO userDTO5 = IntegrationTestHelper.getSelf(
          IntegrationTestHelper.loginUserWithHeader(OWNER_2_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD, userDTO2.getClubId()));
      Assertions.assertEquals(userDTO2.getClubId(), userDTO5.getClubId());*/
    }
  }

  @Test
  void updateSelf() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String token = IntegrationTestHelper.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      final UserDTO self = IntegrationTestHelper.getSelf(token);

      Assertions.assertEquals("Robin", self.getFirstName());
      Assertions.assertEquals("Börjesson", self.getLastName());
      Assertions.assertEquals("1980-01-01", self.getDateOfBirth());

      UpdateUserModel updateUserModel = new UpdateUserModel();
      updateUserModel.setFirstName("FLOYD");
      updateUserModel.setLastName("WEATHER");
      updateUserModel.setDateOfBirth("1900-01-01");

      UserDTO userDTO = IntegrationTestHelper.updateSelf(token, updateUserModel);

      Assertions.assertEquals("FLOYD", userDTO.getFirstName());
      Assertions.assertEquals("WEATHER", userDTO.getLastName());
      Assertions.assertEquals("1900-01-01", userDTO.getDateOfBirth());
    }
  }
}
