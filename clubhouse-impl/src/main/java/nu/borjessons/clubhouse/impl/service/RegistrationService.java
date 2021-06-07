package nu.borjessons.clubhouse.impl.service;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import nu.borjessons.clubhouse.impl.controller.model.request.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.impl.controller.model.request.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.UserDTO;

public interface RegistrationService {

  UserDTO registerChildren(User parent, Club club, @Valid Set<CreateChildRequestModel> childModels);

  UserDTO registerClub(@Valid CreateClubModel clubDetails);

  List<UserDTO> registerFamily(@Valid FamilyRequestModel familyDetails);

  UserDTO registerUser(@Valid CreateUserModel userDetails);
}
