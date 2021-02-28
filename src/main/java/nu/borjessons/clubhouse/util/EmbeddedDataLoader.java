package nu.borjessons.clubhouse.util;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.CreateChildRequestModel;
import nu.borjessons.clubhouse.controller.model.request.FamilyRequestModel;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.data.Club.Type;
import nu.borjessons.clubhouse.dto.UserDTO;
import nu.borjessons.clubhouse.service.RegistrationService;

import javax.annotation.PostConstruct;
import java.util.*;

@Component
@Profile("local")
@Slf4j
@RequiredArgsConstructor
public class EmbeddedDataLoader {

private final RegistrationService registrationService;
	
	@PostConstruct
	private void loadData() {
		CreateUserModel owner = new CreateUserModel();
		owner.setFirstName("Robin");
		owner.setLastName("Börjesson");
		owner.setDateOfBirth("1980-01-01");
		owner.setClubId("dummy");
		owner.setEmail("robin.b@outlook.com");
		owner.setPassword("password");
		
		CreateClubModel clubModel = new CreateClubModel();
		clubModel.setName("Borjessons IK");
		clubModel.setType(Type.SPORT);
		clubModel.setOwner(owner);
		
		UserDTO dto = registrationService.registerClub(clubModel);
		log.info("Created club: {} and user {}",dto.getClubId(), dto.getEmail());

		FamilyRequestModel familyModel = createFamilyRequestModel(dto.getClubId());
		registrationService.registerFamily(familyModel);
		log.info("Created family {}", familyModel);
	}

	private FamilyRequestModel createFamilyRequestModel(String clubId) {
		FamilyRequestModel familyModel = new FamilyRequestModel();
		familyModel.setClubId(clubId);
		CreateUserModel father = new CreateUserModel();
		father.setClubId(clubId);
		father.setEmail("pappa@ex.com");
		father.setFirstName("Pappa");
		father.setLastName("Börjesson");
		father.setDateOfBirth("1982-02-15");
		father.setPassword("password");

		CreateUserModel mother = new CreateUserModel();
		mother.setClubId(clubId);
		mother.setEmail("mamma@ex.com");
		mother.setFirstName("Mamma");
		mother.setLastName("Börjesson");
		mother.setDateOfBirth("1984-07-25");
		mother.setPassword("password");

		CreateChildRequestModel albin = new CreateChildRequestModel();
		albin.setFirstName("Albin");
		albin.setLastName("Börjesson");
		albin.setDateOfBirth("2015-05-13");

		CreateChildRequestModel sixten = new CreateChildRequestModel();
		sixten.setFirstName("Sixten");
		sixten.setLastName("Börjesson");
		sixten.setDateOfBirth("2012-10-24");

		List<CreateUserModel> parents = new ArrayList<>(List.of(father, mother));
		List<CreateChildRequestModel> children = new ArrayList<>(List.of(sixten, albin));

		familyModel.setParents(parents);
		familyModel.setChildren(children);

		return familyModel;
	}
}
