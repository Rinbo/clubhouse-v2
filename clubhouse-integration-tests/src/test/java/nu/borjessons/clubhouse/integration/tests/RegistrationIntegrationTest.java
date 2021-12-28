package nu.borjessons.clubhouse.integration.tests;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.AddressDto;
import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.dto.ClubDto;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;
import nu.borjessons.clubhouse.integration.tests.util.ClubUtil;
import nu.borjessons.clubhouse.integration.tests.util.IntegrationTestHelper;
import nu.borjessons.clubhouse.integration.tests.util.RegistrationUtil;
import nu.borjessons.clubhouse.integration.tests.util.UserUtil;

class RegistrationIntegrationTest {
  public static final String CHILD_NAME = "Generic";

  @Test
  void registerUser() throws Exception {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String firstName = "Dustin";
      CreateUserModel createUserModel = UserUtil.createUserModel(EmbeddedDataLoader.CLUB_ID, firstName);
      UserDto userDTO = RegistrationUtil.registerUser(createUserModel);
      Assertions.assertNotNull(userDTO);
      Assertions.assertEquals(firstName, userDTO.getFirstName());
      validatePersistence(7);
    }
  }

  @Test
  void dadRegistersNewClubChildTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDto daddy = UserUtil.getSelf(token);

      Assertions.assertNotNull(daddy);
      Assertions.assertEquals(2, daddy.getChildren().size());

      UserDto daddyWithOneMoreKid = RegistrationUtil.registerClubChild(EmbeddedDataLoader.CLUB_ID, CHILD_NAME, daddy.getUserId(),
          token);

      Assertions.assertNotNull(daddyWithOneMoreKid);
      Assertions.assertEquals(3, daddyWithOneMoreKid.getChildren().size());
    }
  }

  @Test
  void adminRegistersNewClubChildTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      List<ClubUserDto> users = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, token);
      UserId daddyId = new UserId(UserUtil.getUserIdByEmail(users, EmbeddedDataLoader.POPS_EMAIL).getUserId());

      UserDto daddyWithOneMoreKid = RegistrationUtil.registerClubChild(EmbeddedDataLoader.CLUB_ID, CHILD_NAME, daddyId, token);

      Assertions.assertNotNull(daddyWithOneMoreKid);
      Assertions.assertEquals(3, daddyWithOneMoreKid.getChildren().size());
    }
  }

  @Test
  void registerClubTest() throws JsonProcessingException {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String name = "Owner2";
      CreateClubModel createClubModel = ClubUtil.createClubModel(name);
      final UserDto userDTO = RegistrationUtil.registerClub(createClubModel);
      Assertions.assertEquals(name, userDTO.getFirstName());

      final List<ClubDto> clubs = ClubUtil.getClubs();
      Assertions.assertTrue(clubs.stream().anyMatch(club -> club.name().equals(name + " Sports")));
    }
  }

  @Test
  void registerFamily() throws JsonProcessingException {
    try (ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication()) {
      final String surname = "Garfield";
      FamilyRequestModel familyRequestModel = UserUtil.createFamilyModel(EmbeddedDataLoader.CLUB_ID, surname);
      List<UserDto> userDtos = RegistrationUtil.registerFamily(familyRequestModel);
      Assertions.assertNotNull(userDtos);
      Assertions.assertEquals(2, userDtos.size());

      List<ClubUserDto> clubUsers = validatePersistence(9);
      long familyCount = clubUsers.stream().map(ClubUserDto::getLastName).filter(lastName -> lastName.equals(surname)).count();
      Assertions.assertEquals(3, familyCount);
    }
  }

  @Test
  void parentRegistersChildAndAddsAnotherParent() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String dadToken = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      String childName = "Alva";
      UserDto dad = RegistrationUtil.registerChild(childName, UserUtil.getSelf(dadToken).getUserId(), dadToken);
      BaseUserRecord alva = dad.getChildren().stream().filter(child -> child.firstName().equals(childName)).findFirst().orElseThrow();
      Assertions.assertEquals(3, dad.getChildren().size());
      Assertions.assertEquals(childName, alva.firstName());

      String momToken = UserUtil.loginUser(EmbeddedDataLoader.MOMMY_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDto mom = UserUtil.getSelf(momToken);
      Assertions.assertEquals(2, mom.getChildren().size());

      UserUtil.addParentToChild(dadToken, new UserId(alva.userId()), mom.getUserId());
      Assertions.assertEquals(3, UserUtil.getSelf(momToken).getChildren().size());
    }
  }

  @Test
  void parentUnregistersChild() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      String token = UserUtil.loginUser(EmbeddedDataLoader.POPS_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDto dad = UserUtil.getSelf(token);
      Assertions.assertEquals(2, dad.getChildren().size());

      String childName = "Sixten";
      BaseUserRecord sixten = dad.getChildren().stream().filter(child -> child.firstName().equals(childName)).findFirst().orElseThrow();
      RegistrationUtil.unregisterChild(new UserId(sixten.userId()), dad.getUserId(), token);
      UserDto updatedDad = UserUtil.getSelf(token);
      Assertions.assertEquals(1, updatedDad.getChildren().size());
      Assertions.assertFalse(updatedDad.getChildren().stream().anyMatch(child -> child.firstName().equals(childName)));
    }
  }

  @Test
  void registerUserWithAddress() throws IOException {
    try (EmbeddedPostgres pg = IntegrationTestHelper.startEmbeddedPostgres();
        ConfigurableApplicationContext ignored = IntegrationTestHelper.runSpringApplication(pg.getPort())) {
      UserDto userDto = RegistrationUtil.registerUser(UserUtil.createUserModelWithAddress(EmbeddedDataLoader.CLUB_ID, "Robin"));
      String email = userDto.getEmail();
      String token = UserUtil.loginUser(email, EmbeddedDataLoader.DEFAULT_PASSWORD);
      UserDto self = UserUtil.getSelf(token);
      Assertions.assertEquals(email, self.getEmail());

      Set<AddressDto> addresses = self.getAddresses();
      Assertions.assertEquals(1, addresses.size());

      AddressDto addressDto = addresses.iterator().next();
      Assertions.assertEquals("Rome", addressDto.getCity());
      Assertions.assertEquals("Italy", addressDto.getCountry());
      Assertions.assertEquals("Rome Street 10", addressDto.getStreet());
      Assertions.assertEquals("12345", addressDto.getPostalCode());
    }
  }

  private List<ClubUserDto> validatePersistence(int expectedCount) throws JsonProcessingException {
    String token = UserUtil.loginUser(EmbeddedDataLoader.OWNER_EMAIL, EmbeddedDataLoader.DEFAULT_PASSWORD);
    List<ClubUserDto> clubUsers = UserUtil.getClubUsers(EmbeddedDataLoader.CLUB_ID, token);
    Assertions.assertEquals(expectedCount, clubUsers.size());
    return clubUsers;
  }
}
