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
	
	UserDTO updateUser(User user, String clubId, @Valid UpdateUserModel userDetails);
	
	UserDTO updateUserRoles(User user, String clubId, Set<Role> roles);

	void removeUserFromClub(User user, Club club);
	
	void deleteUser(User user);

	void updateUserLoginTime(String email);

	UserDTO updateUserChildren(User parent, Set<User> children, Club club);

	UserDTO switchClub(User user, Club club);

	UserDTO joinClub(User user, String clubId);
}

