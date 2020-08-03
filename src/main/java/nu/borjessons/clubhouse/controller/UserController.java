package nu.borjessons.clubhouse.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.UpdateUserModel;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.User;
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
		User user = getPrincipal();
		return new UserDTO(user, user.getActiveClub().getClubId());
	}
	
	@PreAuthorize("hasRole('USER')")
	@PutMapping("/principal")
	public UserDTO updateSelf(@RequestBody UpdateUserModel userDetails) {
		User user = getPrincipal();
		return userService.updateUser(user, user.getActiveClub().getClubId(), userDetails);
	}
	
	/*
	 * Administrator routes
	 */
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{userId}")
	public UserDTO getUser(@PathVariable String userId) {
		Club club = getPrincipal().getActiveClub();
		return new UserDTO(club.getUser(userId), club.getClubId());
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{userId}")
	public UserDTO updateUser(@PathVariable String userId, @RequestBody UpdateUserModel userDetails) {
		Club club = getPrincipal().getActiveClub();
		return userService.updateUser(club.getUser(userId), club.getClubId(), userDetails);
	}
	
	
}
