package nu.borjessons.clubhouse.controller;

import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.UserDTO;
import nu.borjessons.clubhouse.repository.UserRepository;

@RequestMapping("/users")
@RequiredArgsConstructor
@RestController
public class UserController extends AbstractController {
	
	private final UserRepository userRepository;
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/{userId}")
	public UserDTO getUser(@PathVariable long userId) {
		Optional<User> maybeUser = userRepository.findById(userId);
		return new UserDTO(getOrThrow(maybeUser));
	}
}
