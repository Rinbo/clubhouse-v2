package nu.borjessons.clubhouse.config;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.data.Club.Type;
import nu.borjessons.clubhouse.dto.UserDTO;
import nu.borjessons.clubhouse.service.RegistrationService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
@Profile("test")
public class InitialDataLoader {

  private final RegistrationService registrationService;

  @PostConstruct
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

    System.out.println(dto.getClubId());
  }
}
