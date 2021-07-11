package nu.borjessons.clubhouse.impl.service;

import java.util.List;

import javax.validation.Valid;

import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;

public interface RegistrationService {
  ClubUserDTO registerChildren(String userId, String clubId, @Valid List<CreateChildRequestModel> childModels);

  ClubUserDTO registerClub(@Valid CreateClubModel clubDetails);

  ClubUserDTO registerClub(@Valid CreateClubModel clubDetails, String clubId);

  List<ClubUserDTO> registerFamily(@Valid FamilyRequestModel familyDetails);

  ClubUserDTO registerUser(@Valid CreateUserModel userDetails);
}
