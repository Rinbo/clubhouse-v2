package nu.borjessons.clubhouse.controller;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
		return new UserDTO(user, user.getActiveClubId());
	}
	
	@PreAuthorize("hasRole('USER')")
	@PutMapping("/principal")
	public UserDTO updateSelf(@RequestBody UpdateUserModel userDetails) {
		User user = getPrincipal();
		return userService.updateUser(user, user.getActiveClubId(), userDetails);
	}
	
	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/principal")
	public void deleteSelf() {
		User user = getPrincipal();
		userService.deleteUser(user);
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
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/children/{userId}")
	public UserDTO updateUserChildren(@PathVariable String userId, @RequestBody Set<String> childrenIds) {
		Club club = getPrincipal().getActiveClub();
		User parent = club.getUser(userId);
		Set<User> clubChildren = club.getManagedUsers();
		Set<User> validatedChildren = clubChildren.stream().filter(child -> childrenIds.contains(child.getUserId())).collect(Collectors.toSet());
		return userService.updateUserChildren(parent, validatedChildren, club);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/remove/{userId}")
	public void removeUserFromClub(@PathVariable String userId) {
		Club club = getPrincipal().getActiveClub();
		User user = club.getUser(userId);
		userService.removeUserFromClub(user, club);
	}
	
	
}
