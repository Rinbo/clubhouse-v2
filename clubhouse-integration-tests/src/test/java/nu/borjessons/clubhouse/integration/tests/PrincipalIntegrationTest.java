package nu.borjessons.clubhouse.integration.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
class PrincipalIntegrationTest {
  public static final String OWNER_2_EMAIL = "owner2@ex.com";

  @Test
  void registerClubAndSwitchTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      // Fetch clubId for default club
      UserDTO userDTO1 = IntegrationTestHelper.getSelf(
          IntegrationTestHelper.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD));

      // Create Second user and club
      CreateClubModel createClubModel = getCreateClubModel();
      UserDTO userDTO2 = IntegrationTestHelper.registerClub(createClubModel);

      // Second user joins first club
      UserDTO userDTO3 = IntegrationTestHelper.joinClub(
          IntegrationTestHelper.loginUser(OWNER_2_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD), userDTO1.getClubId());

      // Assert that active club gets set to first club when joining
      UserDTO userDTO4 = IntegrationTestHelper.getSelf(IntegrationTestHelper.loginUser(OWNER_2_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD));
      Assertions.assertEquals(userDTO3.getClubId(), userDTO4.getClubId());

      // Login with header set to second club and verify that second club is not the active club for second user
      UserDTO userDTO5 = IntegrationTestHelper.getSelf(
          IntegrationTestHelper.loginUserWithHeader(OWNER_2_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD, userDTO2.getClubId()));
      Assertions.assertEquals(userDTO2.getClubId(), userDTO5.getClubId());
    }
  }

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
}
