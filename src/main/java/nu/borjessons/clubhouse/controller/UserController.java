package nu.borjessons.clubhouse.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.dto.UserDTO;
import nu.borjessons.clubhouse.service.ClubhouseAbstractService;
import nu.borjessons.clubhouse.service.UserService;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController extends ClubhouseAbstractService {
	
	private final UserService userService;
	
	/*
	 * Principal routes
	 */
	
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/principal")
	public UserDTO getSelf() {
		return new UserDTO(getPrincipal());
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{userId}")
	public UserDTO getUser(@PathVariable String userId) {
		return new UserDTO(getPrincipal().getActiveClub().getUser(userId));
	}
}
