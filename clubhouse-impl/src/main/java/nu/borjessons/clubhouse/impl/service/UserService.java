package nu.borjessons.clubhouse.impl.service;

import java.util.List;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetailsService;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;

public interface UserService extends UserDetailsService {

  UserDTO createUser(User user);

  List<UserDTO> createUsers(List<User> users);

  void deleteUser(User user);

  User getUserByEmail(String username);

  void removeUserFromClub(User user, Club club);

  UserDTO updateUser(User user);

  UserDTO updateUser(User user, UpdateUserModel userDetails);

  UserDTO updateUser(User user, Club club, AdminUpdateUserModel userDetails);

  UserDTO updateUserChildren(User parent, Set<User> children, Club club);

  void updateUserLoginTime(String email);
}
