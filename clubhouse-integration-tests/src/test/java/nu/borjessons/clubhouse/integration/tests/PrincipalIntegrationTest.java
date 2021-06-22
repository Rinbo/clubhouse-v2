package nu.borjessons.clubhouse.integration.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;

class PrincipalIntegrationTest {

  @Test
  void authTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String adminToken = IntegrationTestHelper.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<UserDTO> users = IntegrationTestHelper.getClubUsers(EmbeddedDataLoader.CLUB1_ID, adminToken);
      Assertions.assertNotNull(users);
      Assertions.assertEquals(5, users.size());

      final String userToken = IntegrationTestHelper.loginUser("pops@ex.com", EmbeddedDataLoader.DEFAULT_PASSWORD);

      try {
        IntegrationTestHelper.getClubUsers(EmbeddedDataLoader.CLUB1_ID, userToken);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
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
