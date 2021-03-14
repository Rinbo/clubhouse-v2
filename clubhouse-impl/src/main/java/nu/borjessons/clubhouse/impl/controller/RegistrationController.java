package nu.borjessons.clubhouse.impl.controller;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.impl.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.impl.controller.model.request.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.security.SecurityConstants;
import nu.borjessons.clubhouse.impl.service.RegistrationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@RestController
public class RegistrationController extends AbstractController {

  private final RegistrationService registrationService;

  @PostMapping(SecurityConstants.CLUB_REGISTRATION_URL)
  public UserDTO registerClub(@Valid @RequestBody CreateClubModel clubDetails) {

    return registrationService.registerClub(clubDetails);
  }

  @PostMapping(SecurityConstants.USER_REGISTRATION_URL)
  public UserDTO registerUser(@Valid @RequestBody CreateUserModel userDetails) {

    return registrationService.registerUser(userDetails);
  }

  @PostMapping(SecurityConstants.FAMILY_REGISTRATION_URL)
  public List<UserDTO> registerFamily(@Valid @RequestBody FamilyRequestModel familyDetails) {
    return registrationService.registerFamily(familyDetails);
  }

  @PreAuthorize("hasRole('USER')")
  @PostMapping("register/principal/children")
  public UserDTO selfRegisterChildren(
      @Valid @RequestBody Set<CreateChildRequestModel> childModels) {
    User principal = getPrincipal();
    return registrationService.registerChildren(
        principal, principal.getActiveClub().getClubId(), childModels);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("register/children/{parentId}")
  public UserDTO registerChildren(
      @PathVariable String parentId, @Valid @RequestBody Set<CreateChildRequestModel> childModels) {
    Club club = getPrincipal().getActiveClub();
    User parent = club.getUser(parentId);

    return registrationService.registerChildren(parent, club.getClubId(), childModels);
  }
}
