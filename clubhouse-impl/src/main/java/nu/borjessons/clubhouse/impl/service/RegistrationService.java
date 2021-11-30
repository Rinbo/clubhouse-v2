package nu.borjessons.clubhouse.impl.service;

import java.util.List;

import javax.validation.Valid;

import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;

public interface RegistrationService {
  UserDto registerClubChildren(String userId, String clubId, @Valid List<CreateChildRequestModel> childModels);

  UserDto registerClub(@Valid CreateClubModel clubDetails);

  UserDto registerClub(@Valid CreateClubModel clubDetails, String clubId);

  List<UserDto> registerFamily(@Valid FamilyRequestModel familyDetails);

  UserDto registerUser(@Valid CreateUserModel userDetails);

  UserDto registerUser(@Valid CreateUserModel userDetails, String userId);

  UserDto registerChild(String parentId, CreateChildRequestModel childModel);

  UserDto unregisterChild(String childId, String parentId);
}
