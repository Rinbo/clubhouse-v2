package nu.borjessons.clubhouse.impl.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.data.Club.Type;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.AddressModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.service.RegistrationService;

@Component
@Profile({"local", "test"})
@Slf4j
@RequiredArgsConstructor
public class EmbeddedDataLoader {
  public static final String BORJESSON = "Börjesson";
  public static final String CLUB1_ID = "club1";
  public static final String DEFAULT_PASSWORD = "password";
  public static final String OWNER_EMAIL = "owner@ex.com";

  private final RegistrationService registrationService;

  private FamilyRequestModel createFamilyRequestModel(String clubId) {
    FamilyRequestModel familyModel = new FamilyRequestModel();
    familyModel.setClubId(clubId);
    CreateUserModel father = new CreateUserModel();
    father.setClubId(clubId);
    father.setEmail("pops@ex.com");
    father.setFirstName("Pappa");
    father.setLastName(BORJESSON);
    father.setDateOfBirth("1982-02-15");
    father.setPassword(DEFAULT_PASSWORD);

    CreateUserModel mother = new CreateUserModel();
    mother.setClubId(clubId);
    mother.setEmail("mommy@ex.com");
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

  @PostConstruct
  private void loadData() {
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
    owner.setAddresses(Set.of(addressModel));

    CreateClubModel clubModel = new CreateClubModel();
    clubModel.setName("Fritiof Sports");
    clubModel.setType(Type.SPORT);
    clubModel.setOwner(owner);

    UserDTO dto = registrationService.registerClub(clubModel, CLUB1_ID);

    String clubId = dto.getClubs()
        .stream()
        .findFirst()
        .orElseThrow()
        .getClubId();

    log.info("Created club: {} and user {}", clubId, dto.getEmail());

    FamilyRequestModel familyModel = createFamilyRequestModel(clubId);
    registrationService.registerFamily(familyModel);
    log.info("Created family {}", familyModel);
  }
}
