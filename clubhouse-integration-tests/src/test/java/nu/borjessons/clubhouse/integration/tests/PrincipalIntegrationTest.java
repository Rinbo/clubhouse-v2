package nu.borjessons.clubhouse.integration.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.controller.model.request.UpdateUserModel;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;

class PrincipalIntegrationTest {
  private static String getClubId(UserDTO userDTO) {
    return userDTO.getClubs().stream().findFirst().orElseThrow().getClubId();
  }

  @Test
  void getClubById() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String token = IntegrationTestHelper.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      final UserDTO self = IntegrationTestHelper.getSelf(token);
      final String clubId = getClubId(self);
      final ClubDTO club = IntegrationTestHelper.getClub(clubId, token);
      Assertions.assertNotNull(club);
      Assertions.assertEquals(clubId, club.getClubId());
    }
  }

  @Test
  void getClubUsers() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String token = IntegrationTestHelper.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      final UserDTO self = IntegrationTestHelper.getSelf(token);
      final List<UserDTO> users = IntegrationTestHelper.getClubUsers(getClubId(self), token);
      Assertions.assertNotNull(users);
      Assertions.assertEquals(5, users.size());
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
