package nu.borjessons.clubhouse.impl.service;

import java.util.List;

import javax.validation.Valid;

import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;

public interface RegistrationService {
  UserDTO registerClubChildren(String userId, String clubId, @Valid List<CreateChildRequestModel> childModels);

  UserDTO registerClub(@Valid CreateClubModel clubDetails);

  UserDTO registerClub(@Valid CreateClubModel clubDetails, String clubId);

  List<UserDTO> registerFamily(@Valid FamilyRequestModel familyDetails);

  UserDTO registerUser(@Valid CreateUserModel userDetails);

  UserDTO registerUser(@Valid CreateUserModel userDetails, String userId);

  UserDTO registerChild(String parentId, CreateChildRequestModel childModel);
}
