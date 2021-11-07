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

import nu.borjessons.clubhouse.impl.dto.BaseUserDTO;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.ClubUtil;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.RegistrationUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class ClubUserIntegrationTest {
  private static void validateEquals(AdminUpdateUserModel updateUserModel, ClubUserDTO clubUserDTO) {
    Assertions.assertEquals(updateUserModel.getFirstName(), clubUserDTO.getFirstName());
    Assertions.assertEquals(updateUserModel.getLastName(), clubUserDTO.getLastName());
    Assertions.assertEquals(updateUserModel.getDateOfBirth(), clubUserDTO.getDateOfBirth());
    Assertions.assertEquals(updateUserModel.getRoles(), new HashSet<>(clubUserDTO.getRoles()));
  }

  @Test
  void getClubUserPrincipal() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      final String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final ClubUserDTO clubUserDTO = UserUtil.getClubUserPrincipal(EmbeddedDataLoader.CLUB_ID, token);
      Assertions.assertNotNull(clubUserDTO);
      Assertions.assertEquals("pops@ex.com", clubUserDTO.getEmail());
      Assertions.assertEquals(EmbeddedDataLoader.CLUB_ID, clubUserDTO.getClubId());
      Assertions.assertEquals("Fritiof Sports", clubUserDTO.getClubName());
      Assertions.assertEquals("Pappa", clubUserDTO.getFirstName());
      Assertions.assertEquals("Börjesson", clubUserDTO.getLastName());
      Assertions.assertEquals(2, clubUserDTO.getChildrenIds().size());
    }
  }

  @Test
  void adminGetsLeadersTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> clubUserDTOs = ClubUtil.getClubLeaders(EmbeddedDataLoader.CLUB_ID, ownerToken);
      Assertions.assertEquals(3, clubUserDTOs.size());
    }
  }

  @Test
  void throwForbiddenExceptionWhenUserGetsLeadersTest() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String userToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      try {
        ClubUtil.getClubLeaders(EmbeddedDataLoader.CLUB_ID, userToken);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
    }
  }

  @Test
  void adminGetsAllClubUsersTest() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> clubUserDTOs = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken);
      Assertions.assertEquals(6, clubUserDTOs.size());
    }
  }

  @Test
  void throwForbiddenExceptionWhenUserGetsAllClubUsersTest() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String userToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      try {
        UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, userToken);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
    }
  }

  @Test
  void addExistingChildToUser() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> clubUsers = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken);
      final ClubUserDTO papaClubUser = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.POPS_EMAIL);
      final ClubUserDTO mamaClubUser = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.MOMMY_EMAIL);
      final UserDTO papaUser = RegistrationUtil.registerChild(EmbeddedDataLoader.CLUB_ID, "Kevin", papaClubUser.getUserId(), ownerToken);
      final ClubUserDTO updatedMamaClubUser = UserUtil.addExistingChildToClubUser(EmbeddedDataLoader.CLUB_ID, ownerToken, mamaClubUser.getUserId(), List.of(
          getDiffEntry(papaClubUser.getChildrenIds().stream()
              .map(BaseUserDTO::userId)
              .collect(Collectors.toSet()), papaUser.getChildrenIds()
              .stream()
              .map(BaseUserDTO::userId)
              .collect(Collectors.toSet()))));

      Assertions.assertEquals(papaUser.getChildrenIds(), updatedMamaClubUser.getChildrenIds());
    }
  }

  @Test
  void getClubUser() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> clubUsers = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken);
      final ClubUserDTO papa = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.POPS_EMAIL);
      final String papaUserId = papa.getUserId();

      Assertions.assertEquals(papa, UserUtil.getUser(EmbeddedDataLoader.CLUB_ID, ownerToken, papaUserId));

      final String papaToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      try {
        UserUtil.getUser(EmbeddedDataLoader.CLUB_ID, papaToken, papaUserId);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
    }
  }

  @Test
  void getClubUsersByAgeRange() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> children = UserUtil.getClubUsersByAge(EmbeddedDataLoader.CLUB_ID, ownerToken, 5, 12);
      Assertions.assertEquals(2, children.size());

      final List<ClubUserDTO> grownUps = UserUtil.getClubUsersByAge(EmbeddedDataLoader.CLUB_ID, ownerToken, 20, 100);
      Assertions.assertEquals(3, grownUps.size());
    }
  }

  @Test
  void parentJoinsClubAndBringsNoChildren() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(1, ClubUtil.getMyClubs(token).size());

      RegistrationUtil.registerClub(ClubUtil.createClubModel("Judo"));
      ClubDTO clubDTO = ClubUtil.getClubByPathName("judo-sports");

      UserDTO pops = UserUtil.getSelf(token);

      ClubUserDTO clubUserDTO = UserUtil.addClubUser(clubDTO.getClubId(), pops.getUserId(), token, Set.of());
      Assertions.assertEquals(2, ClubUtil.getMyClubs(token).size());
      Assertions.assertEquals(2, UserUtil.getClubUsers(clubDTO.getClubId(), token).size());
      Assertions.assertFalse(clubUserDTO.getRoles().contains(Role.PARENT));
    }
  }

  @Test
  void parentJoinsClubAndRetainsAllChildren() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(1, ClubUtil.getMyClubs(token).size());

      RegistrationUtil.registerClub(ClubUtil.createClubModel("Judo"));
      ClubDTO clubDTO = ClubUtil.getClubByPathName("judo-sports");

      UserDTO pops = UserUtil.getSelf(token);

      ClubUserDTO clubUserDTO = UserUtil.addClubUser(clubDTO.getClubId(), pops.getUserId(), token,
          pops.getChildrenIds()
              .stream()
              .map(BaseUserDTO::userId)
              .collect(Collectors.toSet()));

      Assertions.assertEquals(2, ClubUtil.getMyClubs(token).size());
      Assertions.assertEquals(4, UserUtil.getClubUsers(clubDTO.getClubId(), token).size());
      Assertions.assertTrue(clubUserDTO.getRoles().contains(Role.PARENT));
    }
  }

  @Test
  void parentJoinsClubAndAddsOnlyOneChild() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(1, ClubUtil.getMyClubs(token).size());

      RegistrationUtil.registerClub(ClubUtil.createClubModel("Judo"));
      ClubDTO clubDTO = ClubUtil.getClubByPathName("judo-sports");

      UserDTO pops = UserUtil.getSelf(token);

      ClubUserDTO clubUserDTO = UserUtil.addClubUser(clubDTO.getClubId(), pops.getUserId(), token,
          pops.getChildrenIds().stream().map(BaseUserDTO::userId).limit(1).collect(Collectors.toSet()));

      Assertions.assertEquals(2, ClubUtil.getMyClubs(token).size());
      Assertions.assertEquals(3, UserUtil.getClubUsers(clubDTO.getClubId(), token).size());
      Assertions.assertTrue(clubUserDTO.getRoles().contains(Role.PARENT));
    }
  }

  @Test
  void adminRemovesClubUser() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      final List<ClubUserDTO> clubUsers = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken);
      final ClubUserDTO papa = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.POPS_EMAIL);
      final ClubUserDTO mommy = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.MOMMY_EMAIL);

      UserUtil.removeClubUser(EmbeddedDataLoader.CLUB_ID, ownerToken, papa.getUserId());
      Assertions.assertEquals(5, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken).size());

      UserUtil.removeClubUser(EmbeddedDataLoader.CLUB_ID, ownerToken, mommy.getUserId());
      Assertions.assertEquals(4, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken).size());
    }
  }

  @Test
  void userRemovesHimself() throws JsonProcessingException {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String token = UserUtil.loginUser(EmbeddedDataLoader.USER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(1, ClubUtil.getMyClubs(token).size());
      UserUtil.removeClubUser(EmbeddedDataLoader.CLUB_ID, token, EmbeddedDataLoader.USER_ID);
      Assertions.assertEquals(0, ClubUtil.getMyClubs(token).size());

      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      List<ClubUserDTO> clubUsers = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken);

      // User tries to remove another user which is forbidden
      try {
        UserUtil.removeClubUser(EmbeddedDataLoader.CLUB_ID, token, UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.POPS_EMAIL).getUserId());
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
    }
  }

  @Test
  void updateClubUser() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> clubUsers = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, ownerToken);
      final ClubUserDTO papaBase = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.POPS_EMAIL);
      AdminUpdateUserModel updateUserModel = UserUtil.createAdminUpdateModel("Erik", "Johnson", "2000-01-01",
          Set.of(Role.PARENT, Role.LEADER, Role.USER));

      validateEquals(updateUserModel, UserUtil.updateClubUser(updateUserModel, EmbeddedDataLoader.CLUB_ID, ownerToken, papaBase.getUserId()));
    }
  }

  @Test
  void adminFailsAttemptToHackSystemAdmin() throws Exception {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      AdminUpdateUserModel updateUserModel = UserUtil.createAdminUpdateModel("Erik", "Johnson", "2000-01-01",
          Set.of(Role.PARENT, Role.LEADER, Role.USER, Role.SYSTEM_ADMIN));

      ClubUserDTO clubUserDTO = UserUtil.updateClubUser(updateUserModel, EmbeddedDataLoader.CLUB_ID, ownerToken, EmbeddedDataLoader.USER_ID);
      Assertions.assertFalse(clubUserDTO.getRoles().contains(Role.SYSTEM_ADMIN));
    }
  }

  @Test
  void userJoinsAnotherClubAndThenActivatesChildren() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      final String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      Assertions.assertEquals(1, ClubUtil.getMyClubs(token).size());

      RegistrationUtil.registerClub(ClubUtil.createClubModel("Judo"));
      ClubDTO clubDTO = ClubUtil.getClubByPathName("judo-sports");

      UserDTO pops = UserUtil.getSelf(token);

      String clubId = clubDTO.getClubId();
      String userId = pops.getUserId();
      ClubUserDTO clubUserDTO = UserUtil.addClubUser(clubId, userId, token, Set.of());

      Assertions.assertEquals(2, ClubUtil.getMyClubs(token).size());
      Assertions.assertEquals(2, UserUtil.getClubUsers(clubId, token).size());
      Assertions.assertFalse(clubUserDTO.getRoles().contains(Role.PARENT));

      ClubUserDTO updatedClubUserDTO = UserUtil.activateChildren(clubId, userId, token,
          pops.getChildrenIds()
              .stream()
              .map(BaseUserDTO::userId)
              .collect(Collectors.toSet()));

      Assertions.assertEquals(2, ClubUtil.getMyClubs(token).size());
      Assertions.assertEquals(4, UserUtil.getClubUsers(clubId, token).size());
      Assertions.assertTrue(updatedClubUserDTO.getRoles().contains(Role.PARENT));
    }
  }

  /**
   * Pre-supposes original set is smaller and that updated set is a super set of original
   */
  private String getDiffEntry(Set<String> originalSet, Set<String> updatedSet) {
    return updatedSet.stream().filter(id -> !originalSet.contains(id)).findFirst().orElseThrow();
  }
}
