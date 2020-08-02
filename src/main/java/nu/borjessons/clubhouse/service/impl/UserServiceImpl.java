package nu.borjessons.clubhouse.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.ClubRole.Role;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.UserDTO;
import nu.borjessons.clubhouse.repository.UserRepository;
import nu.borjessons.clubhouse.service.ClubhouseAbstractService;
import nu.borjessons.clubhouse.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ClubhouseAbstractService implements UserService {
	
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		Optional<User> maybeUser = userRepository.findByEmail(username);
		return getOrThrowUNFE(maybeUser, username);
	}

	@Override
	public User getUserByEmail(String email) {
		Optional<User> maybeUser = userRepository.findByEmail(email);
		return getOrThrowUNFE(maybeUser, email);
	}

	@Override
	public void updateUserLoginTime(String email) {
		Optional<User> maybeUser = userRepository.findByEmail(email);
		User user = getOrThrow(maybeUser, email);
		user.setLastLoginTime(LocalDateTime.now());
		userRepository.save(user);
	}

	@Override
	public UserDTO updateUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserDTO updateUserRoles(User user, Set<Role> roles) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeUser(User user, Club club) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteUser(User user) {
		// TODO Auto-generated method stub
		
	}
}
