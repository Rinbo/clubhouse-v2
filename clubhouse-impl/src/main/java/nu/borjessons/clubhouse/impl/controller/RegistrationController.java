package nu.borjessons.clubhouse.impl.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.security.SecurityConstants;
import nu.borjessons.clubhouse.impl.service.RegistrationService;

@RequiredArgsConstructor
@RestController
public class RegistrationController {
  private final RegistrationService registrationService;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/clubs/{clubId}/register/children/{parentId}")
  public UserDTO registerChildren(@AuthenticationPrincipal User principal, @PathVariable String parentId, @PathVariable String clubId,
      @Valid @RequestBody List<CreateChildRequestModel> childModels) {
    Club club = principal.getClubByClubId(clubId).orElseThrow();
    User parent = club.getUser(parentId).orElseThrow();
    return registrationService.registerChildren(parent, club, childModels);
  }

  @PostMapping(SecurityConstants.CLUB_REGISTRATION_URL)
  public UserDTO registerClub(@Valid @RequestBody CreateClubModel clubDetails) {
    return registrationService.registerClub(clubDetails);
  }

  @PostMapping(SecurityConstants.FAMILY_REGISTRATION_URL)
  public List<UserDTO> registerFamily(@Valid @RequestBody FamilyRequestModel familyDetails) {
    return registrationService.registerFamily(familyDetails);
  }

  @PostMapping(SecurityConstants.USER_REGISTRATION_URL)
  public UserDTO registerUser(@Valid @RequestBody CreateUserModel userDetails) {
    return registrationService.registerUser(userDetails);
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("/clubs/{clubId}/register/principal/children")
  public UserDTO selfRegisterChildren(@AuthenticationPrincipal User principal, @PathVariable String clubId,
      @Valid @RequestBody List<CreateChildRequestModel> childModels) {
    return registrationService.registerChildren(principal, principal.getClubByClubId(clubId).orElseThrow(), childModels);
  }
}
