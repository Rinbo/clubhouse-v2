package nu.borjessons.clubhouse.impl.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.security.SecurityUtil;
import nu.borjessons.clubhouse.impl.service.RegistrationService;

@RequiredArgsConstructor
@RestController
public class RegistrationController {
  private final RegistrationService registrationService;

  @PostMapping(SecurityUtil.CLUB_REGISTRATION_URL)
  public UserDTO registerClub(@Valid @RequestBody CreateClubModel clubDetails) {
    return registrationService.registerClub(clubDetails);
  }

  @PostMapping(SecurityUtil.FAMILY_REGISTRATION_URL)
  public List<UserDTO> registerFamily(@Valid @RequestBody FamilyRequestModel familyDetails) {
    return registrationService.registerFamily(familyDetails);
  }

  @PostMapping(SecurityUtil.USER_REGISTRATION_URL)
  public UserDTO registerUser(@Valid @RequestBody CreateUserModel userDetails) {
    return registrationService.registerUser(userDetails);
  }

  @PreAuthorize("hasRole('ADMIN') or #parentId == authentication.principal.userId")
  @PostMapping("/clubs/{clubId}/register-children")
  public UserDTO registerChildren(@PathVariable String clubId, @RequestParam String parentId,
      @Valid @RequestBody List<CreateChildRequestModel> childModels) {
    return registrationService.registerChildren(parentId, clubId, childModels);
  }
}
