package nu.borjessons.clubhouse.service;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import nu.borjessons.clubhouse.controller.model.request.CreateChildRequestModel;
import nu.borjessons.clubhouse.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.controller.model.request.FamilyRequestModel;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.UserDTO;

public interface RegistrationService {

	UserDTO registerClub(@Valid CreateClubModel clubDetails);

	UserDTO registerUser(@Valid CreateUserModel userDetails);
	
	UserDTO registerChildren(User parent, String clubId, @Valid Set<CreateChildRequestModel> childModels);

	List<UserDTO> registerFamily(@Valid FamilyRequestModel familyDetails);

}
