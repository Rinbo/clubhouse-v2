package nu.borjessons.clubhouse.service;

import javax.validation.Valid;

import nu.borjessons.clubhouse.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.dto.UserDTO;

public interface RegistrationService {

	UserDTO registerClub(@Valid CreateClubModel clubDetails);

	UserDTO registerUser(@Valid CreateUserModel userDetails);

}
