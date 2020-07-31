package nu.borjessons.clubhouse.service;

import java.util.Set;

import org.springframework.security.core.userdetails.UserDetailsService;

import nu.borjessons.clubhouse.data.ClubRole.Role;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.UserDTO;

public interface UserService extends UserDetailsService {

	User getUserByEmail(String username);
	
	UserDTO updateUser(User user);
	
	UserDTO updateUserRoles(User user, Set<Role> roles);

	void updateUserLoginTime(String email);
}

