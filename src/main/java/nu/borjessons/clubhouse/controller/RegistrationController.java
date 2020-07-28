package nu.borjessons.clubhouse.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.dto.UserDTO;
import nu.borjessons.clubhouse.security.SecurityConstants;
import nu.borjessons.clubhouse.service.RegistrationService;

@RequiredArgsConstructor
@RestController
public class RegistrationController {

	private final RegistrationService registrationService;
	
	@PostMapping(SecurityConstants.CLUB_REGISTRATION_URL)
	public UserDTO registerClub(@Valid @RequestBody CreateClubModel clubDetails) {
		
		return registrationService.registerClub(clubDetails);
	}
	
}
