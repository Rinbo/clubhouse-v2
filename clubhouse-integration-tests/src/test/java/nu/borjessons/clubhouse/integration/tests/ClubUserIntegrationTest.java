package nu.borjessons.clubhouse.integration.tests;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
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
  void addExistingChildToUser() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> clubUsers = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken);
      final ClubUserDTO papaClubUser = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.POPS_EMAIL);
      final ClubUserDTO mamaClubUser = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.MOMMY_EMAIL);
      final UserDTO papaUser = RegistrationUtil.registerChild(EmbeddedDataLoader.CLUB1_ID, "Kevin", papaClubUser.getUserId(), ownerToken);
      final ClubUserDTO updatedMamaClubUser = UserUtil.addExistingChildToClubUser(EmbeddedDataLoader.CLUB1_ID, ownerToken, mamaClubUser.getUserId(), List.of(
          getDiffEntry(papaClubUser.getChildrenIds(), papaUser.getChildrenIds())));

      Assertions.assertEquals(papaUser.getChildrenIds(), updatedMamaClubUser.getChildrenIds());
    }
  }

  @Test
  void getClubUser() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> clubUsers = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken);
      final ClubUserDTO papa = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.POPS_EMAIL);
      final String papaUserId = papa.getUserId();

      Assertions.assertEquals(papa, UserUtil.getUser(EmbeddedDataLoader.CLUB1_ID, ownerToken, papaUserId));

      final String papaToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      try {
        UserUtil.getUser(EmbeddedDataLoader.CLUB1_ID, papaToken, papaUserId);
      } catch (HttpClientErrorException e) {
        Assertions.assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());
      }
    }
  }

  @Test
  void getClubUsersByAgeRange() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> children = UserUtil.getClubUsersByAge(EmbeddedDataLoader.CLUB1_ID, ownerToken, 5, 12);
      Assertions.assertEquals(2, children.size());

      final List<ClubUserDTO> grownUps = UserUtil.getClubUsersByAge(EmbeddedDataLoader.CLUB1_ID, ownerToken, 20, 100);
      Assertions.assertEquals(3, grownUps.size());
    }
  }

  @Test
  void removeClubUser() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);

      final List<ClubUserDTO> clubUsers = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken);
      final ClubUserDTO papa = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.POPS_EMAIL);
      final ClubUserDTO mommy = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.MOMMY_EMAIL);

      UserUtil.removeClubUser(EmbeddedDataLoader.CLUB1_ID, ownerToken, papa.getUserId());
      Assertions.assertEquals(4, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken).size());

      UserUtil.removeClubUser(EmbeddedDataLoader.CLUB1_ID, ownerToken, mommy.getUserId());
      Assertions.assertEquals(3, UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken).size());
    }
  }

  @Test
  void updateClubUser() throws Exception {
    try (ConfigurableApplicationContext context = IntegrationTestHelper.runSpringApplication()) {
      final String ownerToken = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      final List<ClubUserDTO> clubUsers = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB1_ID, ownerToken);
      final ClubUserDTO papaBase = UserUtil.getUserIdByEmail(clubUsers, EmbeddedDataLoader.POPS_EMAIL);
      AdminUpdateUserModel updateUserModel = UserUtil.createAdminUpdateModel("Erik", "Johnson", "2000-01-01",
          Set.of(Role.PARENT, Role.LEADER, Role.USER));

      validateEquals(updateUserModel, UserUtil.updateClubUser(updateUserModel, EmbeddedDataLoader.CLUB1_ID, ownerToken, papaBase.getUserId()));
    }
  }

  /**
   * Pre-supposes original set is smaller and that updated set is a super set of original
   */
  private String getDiffEntry(Set<String> originalSet, Set<String> updatedSet) {
    return updatedSet.stream().filter(id -> !originalSet.contains(id)).findFirst().orElseThrow();
  }
}
