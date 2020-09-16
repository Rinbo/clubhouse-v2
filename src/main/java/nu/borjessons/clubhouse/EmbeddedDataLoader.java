package nu.borjessons.clubhouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.data.Club.Type;
import nu.borjessons.clubhouse.dto.UserDTO;
import nu.borjessons.clubhouse.repository.ClubRepository;
import nu.borjessons.clubhouse.service.RegistrationService;

@Component
@Profile("local")
@Slf4j
public class EmbeddedDataLoader {
private final RegistrationService registrationService;
	
	@Autowired
	public EmbeddedDataLoader(RegistrationService registrationService, ClubRepository clubRepository) {
		this.registrationService = registrationService;
		loadData();
	}
	
	private void loadData() {
		
		CreateUserModel owner = new CreateUserModel();
		owner.setFirstName("Alle");
		owner.setLastName("Allesson");
		owner.setDateOfBirth("1980-01-01");
		owner.setClubId("dummy");
		owner.setEmail("alle@clubhouse.com");
		owner.setPassword("1234567890");
		
		CreateClubModel clubModel = new CreateClubModel();
		clubModel.setName("Alles IK");
		clubModel.setType(Type.SPORT);
		clubModel.setOwner(owner);
		
		UserDTO dto = registrationService.registerClub(clubModel);
		
		log.info(dto.getClubId());
	}
}
