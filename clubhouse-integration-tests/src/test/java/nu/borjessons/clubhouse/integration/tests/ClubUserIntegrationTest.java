package nu.borjessons.clubhouse.integration.tests;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.util.dev.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.ClubUtil;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.RegistrationUtil;
import nu.borjessons.clubhouse.integration.tests.util.RestUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ClubUserIntegrationTest {
  private static void validateEquals(AdminUpdateUserModel updateUserModel, ClubUserDto clubUserDTO) {
    Assertions.assertEquals(updateUserModel.getFirstName(), clubUserDTO.getFirstName());
    Assertions.assertEquals(updateUserModel.getLastName(), clubUserDTO.getLastName());
    Assertions.assertEquals(updateUserModel.getDateOfBirth(), clubUserDTO.getDateOfBirth());
    Assertions.assertEquals(updateUserModel.getRoles(), new HashSet<>(clubUserDTO.getRoles()));
  }

  @Test
  void adminFailsAttemptToHackSystemAdmin() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      AdminUpdateUserModel updateUserModel = UserUtil.createAdminUpdateModel("Erik", "Johnson", "2000-01-01",
          Set.of(Role.PARENT, Role.LEADER, Role.USER, Role.SYSTEM_ADMIN));

      ClubUserDto clubUserDTO = UserUtil.updateClubUser(updateUserModel, EmbeddedDataLoader.CLUB_ID, ownerToken, EmbeddedDataLoader.USER_ID);
      Assertions.assertFalse(clubUserDTO.getRoles().contains(Role.SYSTEM_ADMIN));
    }
  }

  @Test
  void adminGetsAllClubUsersTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      List<ClubUserDto> clubUserDtos = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken);
      Assertions.assertEquals(6, clubUserDtos.size());
    }
  }

  @Test
  void adminGetsLeadersTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      List<ClubUserDto> clubUserDtos = ClubUtil.getClubLeaders(EmbeddedDataLoader.CLUB_ID, ownerToken);
      Assertions.assertEquals(3, clubUserDtos.size());
    }
  }

  @Test
  void adminRemovesClubUser() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      ClubUserDto papa = UserUtil.getUserIdByEmail(EmbeddedDataLoader.CLUB_ID, EmbeddedDataLoader.POPS_EMAIL, context);
      ClubUserDto mommy = UserUtil.getUserIdByEmail(EmbeddedDataLoader.CLUB_ID, EmbeddedDataLoader.MOMMY_EMAIL, context);

      UserUtil.removeClubUser(EmbeddedDataLoader.CLUB_ID, ownerToken, papa.getUserId());
      Assertions.assertEquals(5, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken).size());

      UserUtil.removeClubUser(EmbeddedDataLoader.CLUB_ID, ownerToken, mommy.getUserId());
      Assertions.assertEquals(4, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken).size());
    }
  }

  @Test
  void getClubUser() {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      ClubUserDto papa = UserUtil.getUserIdByEmail(EmbeddedDataLoader.CLUB_ID, EmbeddedDataLoader.POPS_EMAIL, context);
      UserId papaUserId = papa.getUserId();

      Assertions.assertEquals(papa, UserUtil.getUser(EmbeddedDataLoader.CLUB_ID, ownerToken, papaUserId));

      String papaToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      RestUtil.verifyForbiddenAccess(() -> UserUtil.getUser(EmbeddedDataLoader.CLUB_ID, papaToken, papaUserId));
    }
  }

  @Test
  void getClubUserPrincipal() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDto userDto = UserUtil.getSelf(token);
      List<BaseUserRecord> children = UserUtil.getClubUsersSubset(EmbeddedDataLoader.CLUB_ID, token, UserUtil.getUserIdsAndSort(userDto.getChildren()));
      ClubUserDto clubUserDTO = UserUtil.getClubUserPrincipal(EmbeddedDataLoader.CLUB_ID, token);
      Assertions.assertNotNull(clubUserDTO);
      Assertions.assertEquals(EmbeddedDataLoader.CLUB_ID, clubUserDTO.getClub().clubId());
      Assertions.assertEquals("Pappa", clubUserDTO.getFirstName());
      Assertions.assertEquals("Börjesson", clubUserDTO.getLastName());
      Assertions.assertEquals(2, children.size());
    }
  }

  @Test
  void getClubUsersSubset() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDto userDto = UserUtil.getSelf(token);
      List<String> childrenIds = UserUtil.getUserIdsAndSort(userDto.getChildren());
      List<BaseUserRecord> baseUserRecords = UserUtil.getClubUsersSubset(EmbeddedDataLoader.CLUB_ID, token, childrenIds);
      Assertions.assertEquals(childrenIds, UserUtil.getUserIdsAndSort(baseUserRecords));
    }
  }

  @Test
  void getUserEmail() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String popsToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      String adminToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      String userToken = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      RestUtil.verifyForbiddenAccess(() -> UserUtil.getUserEmail(popsToken, EmbeddedDataLoader.CLUB_ID, EmbeddedDataLoader.USER_ID));
      Assertions.assertEquals(EmbeddedDataLoader.USER_EMAIL, UserUtil.getUserEmail(userToken, EmbeddedDataLoader.CLUB_ID, EmbeddedDataLoader.USER_ID));
      Assertions.assertEquals(EmbeddedDataLoader.USER_EMAIL, UserUtil.getUserEmail(adminToken, EmbeddedDataLoader.CLUB_ID, EmbeddedDataLoader.USER_ID));

      UserUtil.updateSelf(userToken, UserUtil.createUpdateModel("Hej", "Hello", "2000-01-01", true));
      Assertions.assertEquals(EmbeddedDataLoader.USER_EMAIL, UserUtil.getUserEmail(popsToken, EmbeddedDataLoader.CLUB_ID, EmbeddedDataLoader.USER_ID));
    }
  }

  @Test
  void parentJoinsClubAndAddsOnlyOneChild() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(1, ClubUtil.getMyClubs(token).size());

      RegistrationUtil.registerClub(ClubUtil.createClubModel("Judo"));
      ClubRecord clubRecord = ClubUtil.getClubByPathName("judo-sports");

      UserDto pops = UserUtil.getSelf(token);

      ClubUserDto clubUserDto = UserUtil.addClubUser(clubRecord.clubId(), pops.getUserId(), token,
          pops.getChildren().stream().map(BaseUserRecord::userId).limit(1).collect(Collectors.toSet()));

      Assertions.assertEquals(2, ClubUtil.getMyClubs(token).size());
      Assertions.assertEquals(3, UserUtil.getClubUsers(clubRecord.clubId(), token).size());
      Assertions.assertTrue(clubUserDto.getRoles().contains(Role.PARENT));
    }
  }

  @Test
  void parentJoinsClubAndBringsNoChildren() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(1, ClubUtil.getMyClubs(token).size());

      RegistrationUtil.registerClub(ClubUtil.createClubModel("Judo"));
      ClubRecord clubRecord = ClubUtil.getClubByPathName("judo-sports");

      UserDto pops = UserUtil.getSelf(token);

      ClubUserDto clubUserDTO = UserUtil.addClubUser(clubRecord.clubId(), pops.getUserId(), token, Set.of());
      Assertions.assertEquals(2, ClubUtil.getMyClubs(token).size());
      Assertions.assertEquals(2, UserUtil.getClubUsers(clubRecord.clubId(), token).size());
      Assertions.assertFalse(clubUserDTO.getRoles().contains(Role.PARENT));
    }
  }

  @Test
  void parentJoinsClubAndRetainsAllChildren() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(1, ClubUtil.getMyClubs(token).size());

      RegistrationUtil.registerClub(ClubUtil.createClubModel("Judo"));
      ClubRecord clubRecord = ClubUtil.getClubByPathName("judo-sports");

      UserDto pops = UserUtil.getSelf(token);

      ClubUserDto clubUserDTO = UserUtil.addClubUser(clubRecord.clubId(), pops.getUserId(), token,
          pops.getChildren()
              .stream()
              .map(BaseUserRecord::userId)
              .collect(Collectors.toSet()));

      Assertions.assertEquals(2, ClubUtil.getMyClubs(token).size());
      Assertions.assertEquals(4, UserUtil.getClubUsers(clubRecord.clubId(), token).size());
      Assertions.assertTrue(clubUserDTO.getRoles().contains(Role.PARENT));
    }
  }

  @Test
  void parentRemovesHisChildrenFromClub() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDto pops = UserUtil.getSelf(token);
      Assertions.assertEquals(6, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, token).size());

      ClubUserDto clubUserDTO = UserUtil.removeClubChildren(EmbeddedDataLoader.CLUB_ID, pops.getUserId(), token,
          pops.getChildren().stream().map(BaseUserRecord::userId).collect(Collectors.toSet()));

      Assertions.assertEquals(4, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, token).size());
      Assertions.assertFalse(clubUserDTO.getRoles().contains(Role.PARENT));
    }
  }

  @Test
  void parentRemovesOneChildFromClub() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String dadToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDto pops = UserUtil.getSelf(dadToken);
      Assertions.assertEquals(6, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, dadToken).size());

      ClubUserDto clubUserDTO = UserUtil.removeClubChildren(EmbeddedDataLoader.CLUB_ID, pops.getUserId(), dadToken,
          pops.getChildren().stream().map(BaseUserRecord::userId).limit(1).collect(Collectors.toSet()));

      Assertions.assertEquals(5, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, dadToken).size());
      Assertions.assertTrue(clubUserDTO.getRoles().contains(Role.PARENT));

      String momToken = UserUtil.loginUser(EmbeddedDataLoader.MOMMY_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDto mom = UserUtil.getSelf(momToken);
      List<BaseUserRecord> clubChildren = UserUtil.getClubUsersSubset(EmbeddedDataLoader.CLUB_ID, momToken, UserUtil.getUserIdsAndSort(mom.getChildren()));
      Assertions.assertEquals(1, clubChildren.size());
    }
  }

  @Test
  void throwForbiddenExceptionWhenUserGetsAllClubUsersTest() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      String userToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      try {
        UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, userToken);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
    }
  }

  @Test
  void throwForbiddenExceptionWhenUserGetsLeadersTest() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      String userToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      try {
        ClubUtil.getClubLeaders(EmbeddedDataLoader.CLUB_ID, userToken);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
    }
  }

  @Test
  void updateClubUser() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      ClubUserDto papaBase = UserUtil.getUserIdByEmail(EmbeddedDataLoader.CLUB_ID, EmbeddedDataLoader.POPS_EMAIL, context);
      AdminUpdateUserModel updateUserModel = UserUtil.createAdminUpdateModel("Erik", "Johnson", "2000-01-01",
          Set.of(Role.PARENT, Role.LEADER, Role.USER));

      validateEquals(updateUserModel, UserUtil.updateClubUser(updateUserModel, EmbeddedDataLoader.CLUB_ID, ownerToken, papaBase.getUserId()));
    }
  }

  @Test
  void userJoinsAnotherClubAndThenActivatesChildren() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(1, ClubUtil.getMyClubs(token).size());

      RegistrationUtil.registerClub(ClubUtil.createClubModel("Judo"));
      ClubRecord clubRecord = ClubUtil.getClubByPathName("judo-sports");

      UserDto pops = UserUtil.getSelf(token);

      String clubId = clubRecord.clubId();
      UserId userId = pops.getUserId();
      ClubUserDto clubUserDTO = UserUtil.addClubUser(clubId, userId, token, Set.of());

      Assertions.assertEquals(2, ClubUtil.getMyClubs(token).size());
      Assertions.assertEquals(2, UserUtil.getClubUsers(clubId, token).size());
      Assertions.assertFalse(clubUserDTO.getRoles().contains(Role.PARENT));

      ClubUserDto updatedClubUserDto = UserUtil.activateChildren(clubId, userId, token,
          pops.getChildren()
              .stream()
              .map(BaseUserRecord::userId)
              .collect(Collectors.toSet()));

      Assertions.assertEquals(2, ClubUtil.getMyClubs(token).size());
      Assertions.assertEquals(4, UserUtil.getClubUsers(clubId, token).size());
      Assertions.assertTrue(updatedClubUserDto.getRoles().contains(Role.PARENT));
    }
  }

  @Test
  void userRemovesHimself() throws JsonProcessingException {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(1, ClubUtil.getMyClubs(token).size());
      UserUtil.removeClubUser(EmbeddedDataLoader.CLUB_ID, token, EmbeddedDataLoader.USER_ID);
      Assertions.assertEquals(0, ClubUtil.getMyClubs(token).size());

      // User tries to remove another user which is forbidden
      UserId userId = UserUtil.getUserIdByEmail(EmbeddedDataLoader.CLUB_ID, EmbeddedDataLoader.POPS_EMAIL, context).getUserId();
      RestUtil.verifyForbiddenAccess(() -> UserUtil.removeClubUser(EmbeddedDataLoader.CLUB_ID, token, userId));
    }
  }
}
