package nu.borjessons.clubhouse.service;

import nu.borjessons.clubhouse.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.dto.UserDTO;

public interface RegistrationService {

	UserDTO registerClub(CreateClubModel clubDetails);

}
