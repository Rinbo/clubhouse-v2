package nu.borjessons.clubhouse.impl.util.dev;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.Club.Type;
import nu.borjessons.clubhouse.impl.data.RoleEntity;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.dto.TeamDto;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.AddressModel;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.TeamRequestModel;
import nu.borjessons.clubhouse.impl.repository.RoleRepository;
import nu.borjessons.clubhouse.impl.service.ClubUserService;
import nu.borjessons.clubhouse.impl.service.RegistrationService;
import nu.borjessons.clubhouse.impl.service.TeamService;

@Component
@Profile({"test", "dev"})
@Slf4j
@RequiredArgsConstructor
public class EmbeddedDataLoader {
  public static final String BORJESSON = "Börjesson";
  public static final String CLUB_ID = "club1";
  public static final String DEFAULT_PASSWORD = "password";
  public static final String MOMMY_EMAIL = "mommy@ex.com";
  public static final String OWNER_EMAIL = "owner@ex.com";
  public static final String POPS_EMAIL = "pops@ex.com";
  public static final String USER_EMAIL = "user@ex.com";
  public static final UserId USER_ID = new UserId("user1");

  private static FamilyRequestModel createFamilyRequestModel() {
    FamilyRequestModel familyModel = new FamilyRequestModel();
    familyModel.setClubId(CLUB_ID);
    CreateUserModel father = new CreateUserModel();
    father.setClubId(CLUB_ID);
    father.setEmail(POPS_EMAIL);
    father.setFirstName("Pappa");
    father.setLastName(BORJESSON);
    father.setDateOfBirth("1982-02-15");
    father.setPassword(DEFAULT_PASSWORD);

    CreateUserModel mother = new CreateUserModel();
    mother.setClubId(CLUB_ID);
    mother.setEmail(MOMMY_EMAIL);
    mother.setFirstName("Mamma");
    mother.setLastName(BORJESSON);
    mother.setDateOfBirth("1984-07-25");
    mother.setPassword(DEFAULT_PASSWORD);

    CreateChildRequestModel albin = new CreateChildRequestModel();
    albin.setFirstName("Albin");
    albin.setLastName(BORJESSON);
    albin.setDateOfBirth("2015-05-13");

    CreateChildRequestModel sixten = new CreateChildRequestModel();
    sixten.setFirstName("Sixten");
    sixten.setLastName(BORJESSON);
    sixten.setDateOfBirth("2012-10-24");

    List<CreateUserModel> parents = new ArrayList<>(List.of(father, mother));
    List<CreateChildRequestModel> children = new ArrayList<>(List.of(sixten, albin));

    familyModel.setParents(parents);
    familyModel.setChildren(children);

    return familyModel;
  }

  private static CreateUserModel createNormalUser() {
    CreateUserModel user = new CreateUserModel();
    user.setClubId(CLUB_ID);
    user.setEmail(USER_EMAIL);
    user.setFirstName("Normal");
    user.setLastName("User");
    user.setDateOfBirth("2008-01-01");
    user.setPassword(DEFAULT_PASSWORD);
    return user;
  }

  private static CreateUserModel createOwnerModel() {
    final AddressModel addressModel = new AddressModel();
    addressModel.setCity("Gothenburg");
    addressModel.setCountry("Sweden");
    addressModel.setStreet("Elm Street 5");
    addressModel.setPostalCode("666");

    CreateUserModel owner = new CreateUserModel();
    owner.setFirstName("Robin");
    owner.setLastName(BORJESSON);
    owner.setDateOfBirth("1980-01-01");
    owner.setClubId("dummy");
    owner.setEmail(OWNER_EMAIL);
    owner.setPassword(DEFAULT_PASSWORD);
    owner.setAddresses(List.of(addressModel));
    return owner;
  }

  private static TeamRequestModel createTeamModel(UserId leaderId) {
    TeamRequestModel teamRequestModel = new TeamRequestModel();
    teamRequestModel.setName("Cool Team");
    teamRequestModel.setMinAge(5);
    teamRequestModel.setMaxAge(20);
    teamRequestModel.setLeaderIds(List.of(leaderId.toString()));
    return teamRequestModel;
  }

  private static RoleEntity getRoleEntity(Role role) {
    final RoleEntity roleEntity = new RoleEntity();
    roleEntity.setName(role);
    return roleEntity;
  }

  private final ClubUserService clubUserService;
  private final RegistrationService registrationService;
  private final RoleRepository roleRepository;
  private final TeamService teamService;
  private final RoleLoader roleLoader;

  @PostConstruct
  private void loadData() {
    EmbeddedDataUtil.loadRoles(roleRepository);

    CreateUserModel owner = createOwnerModel();

    CreateClubModel clubModel = new CreateClubModel();
    clubModel.setName("Fritiof Sports");
    clubModel.setType(Type.SPORT);
    clubModel.setOwner(owner);

    UserDto ownerDTO = registrationService.registerClub(clubModel, CLUB_ID);
    log.info("Created club: {} and user {}", CLUB_ID, ownerDTO.getEmail());

    CreateUserModel normalUser = createNormalUser();
    UserDto normalUserDto = registrationService.registerUser(normalUser, USER_ID);
    log.info("Created normal user: {}", normalUserDto);

    FamilyRequestModel familyModel = createFamilyRequestModel();
    List<UserDto> parents = registrationService.registerFamily(familyModel);
    log.info("Created family {}", familyModel);

    parents.forEach(parent -> updateRole(parent, Set.of(Role.PARENT, Role.USER, Role.LEADER)));
    UserDto dad = parents.stream().filter(parent -> parent.getEmail().equals(POPS_EMAIL)).findFirst().orElseThrow();
    TeamRequestModel teamModel = createTeamModel(dad.getUserId());

    TeamDto team = teamService.createTeam(CLUB_ID, teamModel);
    ArrayList<UserId> teamMembers = new ArrayList<>(dad.getChildren().stream().map(BaseUserRecord::userId).map(UserId::new).toList());
    teamMembers.add(normalUserDto.getUserId());
    TeamDto updateTeamDto = teamService.updateTeamMembers(CLUB_ID, team.getTeamId(), teamMembers);
    log.info("created team: {}", updateTeamDto);
  }

  private void updateRole(UserDto parent, Set<Role> roles) {
    AdminUpdateUserModel userDetails = new AdminUpdateUserModel();
    userDetails.setFirstName(parent.getFirstName());
    userDetails.setLastName(parent.getLastName());
    userDetails.setDateOfBirth(parent.getDateOfBirth());
    userDetails.setRoles(roles);
    clubUserService.updateUser(parent.getUserId(), CLUB_ID, userDetails);
  }
}
