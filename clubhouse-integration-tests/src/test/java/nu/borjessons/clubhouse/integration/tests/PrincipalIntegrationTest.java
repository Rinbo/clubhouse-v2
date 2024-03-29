package nu.borjessons.clubhouse.integration.tests;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;
import nu.borjessons.clubhouse.impl.util.dev.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class PrincipalIntegrationTest {
  @Test
  void authTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String adminToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      List<ClubUserDto> users = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, adminToken);
      Assertions.assertNotNull(users);
      Assertions.assertEquals(6, users.size());

      String userToken = UserUtil.loginUser("pops@ex.com", EmbeddedDataLoader.DEFAULT_PASSWORD);

      try {
        UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, userToken);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
    }
  }

  /**
   * There are two parents in the club that both share the same set of two children.
   * When Papa deletes himself the children remains in the club because mommy is still there,
   * but once she deletes herself both the children are deleted as well.
   */
  @Test
  void deleteSelf() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(6, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken).size());

      String papaToken = UserUtil.loginUser("pops@ex.com", EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserUtil.deleteSelf(papaToken);

      Assertions.assertEquals(5, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken).size());

      String mamaToken = UserUtil.loginUser("mommy@ex.com", EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserUtil.deleteSelf(mamaToken);

      Assertions.assertEquals(2, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken).size());
    }
  }

  @Test
  void getAllMyClubUsers() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      List<ClubUserDto> clubUserDtos = UserUtil.getPrincipalClubUsers(token);
      Assertions.assertEquals(1, clubUserDtos.size());
      clubUserDtos.forEach(clubUserDTO -> Assertions.assertEquals(EmbeddedDataLoader.USER_ID, clubUserDTO.getUserId()));
    }
  }

  @Test
  void getChildren() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDto userDto = UserUtil.getSelf(token);
      List<String> childrenIds = UserUtil.getUserIdsAndSort(userDto.getChildren());
      List<BaseUserRecord> baseUserRecords = UserUtil.getChildren(token);
      Assertions.assertEquals(childrenIds, UserUtil.getUserIdsAndSort(baseUserRecords));
    }
  }

  @Test
  void getSelf() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDto self = UserUtil.getSelf(ownerToken);
      Assertions.assertNotNull(self);
      Assertions.assertEquals(EmbeddedDataLoader.OWNER_EMAIL, self.getEmail());
    }
  }

  @Test
  void updateSelf() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      UserDto self = UserUtil.getSelf(token);

      Assertions.assertEquals("Robin", self.getFirstName());
      Assertions.assertEquals("Börjesson", self.getLastName());
      Assertions.assertEquals("1980-01-01", self.getDateOfBirth());

      UpdateUserModel updateUserModel = new UpdateUserModel();
      updateUserModel.setFirstName("FLOYD");
      updateUserModel.setLastName("WEATHER");
      updateUserModel.setDateOfBirth("1900-01-01");

      UserDto userDTO = UserUtil.updateSelf(token, updateUserModel);

      Assertions.assertEquals("FLOYD", userDTO.getFirstName());
      Assertions.assertEquals("WEATHER", userDTO.getLastName());
      Assertions.assertEquals("1900-01-01", userDTO.getDateOfBirth());
    }
  }
}
