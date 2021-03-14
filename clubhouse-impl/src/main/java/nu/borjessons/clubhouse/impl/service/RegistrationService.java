package nu.borjessons.clubhouse.impl.service;

import nu.borjessons.clubhouse.impl.controller.model.request.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.impl.controller.model.request.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.UserDTO;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

public interface RegistrationService {

  UserDTO registerClub(@Valid CreateClubModel clubDetails);

  UserDTO registerUser(@Valid CreateUserModel userDetails);

  UserDTO registerChildren(
      User parent, String clubId, @Valid Set<CreateChildRequestModel> childModels);

  List<UserDTO> registerFamily(@Valid FamilyRequestModel familyDetails);
}
