package nu.borjessons.clubhouse.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.repository.UserRepository;
import nu.borjessons.clubhouse.service.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		
		UserDetails user = userRepository.findByEmail(username);
		
		if (user != null ) return user;
		
		throw new UsernameNotFoundException(String.format("User %s could not be found", username));	
	}

	@Override
	public User getUserByEmail(String email) {
		User user = userRepository.findByEmail(email);
		
		if (user != null ) return user;
		
		throw new UsernameNotFoundException(String.format("User %s could not be found", email));
	}
	
}
