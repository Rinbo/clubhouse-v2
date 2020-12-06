package nu.borjessons.clubhouse.util;

import lombok.RequiredArgsConstructor;
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

import javax.annotation.PostConstruct;

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
		
		log.info(String.format("Created club: %s and user %s",dto.getClubId(), dto.getEmail()));
	}
}
