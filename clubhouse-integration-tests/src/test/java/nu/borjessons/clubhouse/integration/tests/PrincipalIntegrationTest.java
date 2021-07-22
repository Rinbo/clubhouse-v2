package nu.borjessons.clubhouse.integration.tests;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class PrincipalIntegrationTest {

  @Test
  void authTest() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String adminToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> users = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, adminToken);
      Assertions.assertNotNull(users);
      Assertions.assertEquals(5, users.size());

      final String userToken = UserUtil.loginUser("pops@ex.com", EmbeddedDataLoader.DEFAULT_PASSWORD);

      try {
        UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, userToken);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
    }
  }

  @Test
  void updateSelf() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      final UserDTO self = UserUtil.getSelf(token);

      Assertions.assertEquals("Robin", self.getFirstName());
      Assertions.assertEquals("Börjesson", self.getLastName());
      Assertions.assertEquals("1980-01-01", self.getDateOfBirth());

      UpdateUserModel updateUserModel = new UpdateUserModel();
      updateUserModel.setFirstName("FLOYD");
      updateUserModel.setLastName("WEATHER");
      updateUserModel.setDateOfBirth("1900-01-01");

      UserDTO userDTO = UserUtil.updateSelf(token, updateUserModel);

      Assertions.assertEquals("FLOYD", userDTO.getFirstName());
      Assertions.assertEquals("WEATHER", userDTO.getLastName());
      Assertions.assertEquals("1900-01-01", userDTO.getDateOfBirth());
    }
  }

  /**
   * There are two parents in the club that both share the same set of two children.
   * When Papa deletes himself the children remains in the club because mommy is still there,
   * but once she deletes herself both the children are deleted as well.
   */
  @Test
  void deleteSelf() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(5, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken).size());

      final String papaToken = UserUtil.loginUser("pops@ex.com", EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserUtil.deleteSelf(papaToken);

      Assertions.assertEquals(4, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken).size());

      final String mamaToken = UserUtil.loginUser("mommy@ex.com", EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserUtil.deleteSelf(mamaToken);

      Assertions.assertEquals(1, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken).size());
    }
  }

  @Test
  void getSelf() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final UserDTO self = UserUtil.getSelf(ownerToken);
      Assertions.assertNotNull(self);
      Assertions.assertEquals(EmbeddedDataLoader.OWNER_EMAIL, self.getEmail());
    }
  }
}
