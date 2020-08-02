package nu.borjessons.clubhouse.service;

import java.util.Set;

import javax.validation.Valid;

import org.springframework.security.core.userdetails.UserDetailsService;

import nu.borjessons.clubhouse.controller.model.request.UpdateUserModel;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.ClubRole.Role;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.UserDTO;

public interface UserService extends UserDetailsService {

	User getUserByEmail(String username);
	
	UserDTO updateUser(User user, @Valid UpdateUserModel userDetails);
	
	UserDTO updateUserRoles(User user, Set<Role> roles);

	void removeUser(User user, Club club);
	
	void deleteUser(User user);

	void updateUserLoginTime(String email);
}

