package nu.borjessons.clubhouse.impl.service;

import java.util.List;

import javax.validation.Valid;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;

public interface RegistrationService {
  UserDto registerClubChildren(UserId userId, String clubId, @Valid List<CreateChildRequestModel> childModels);

  UserDto registerClub(@Valid CreateClubModel clubDetails);

  UserDto registerClub(@Valid CreateClubModel clubDetails, String clubId);

  List<UserDto> registerFamily(@Valid FamilyRequestModel familyDetails);

  UserDto registerUser(@Valid CreateUserModel userDetails);

  UserDto registerUser(@Valid CreateUserModel userDetails, UserId userId);

  UserDto registerChild(UserId parentId, CreateChildRequestModel childModel);

  UserDto unregisterChild(UserId childId, UserId parentId);
}
