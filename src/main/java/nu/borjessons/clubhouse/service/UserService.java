package nu.borjessons.clubhouse.service;

import nu.borjessons.clubhouse.controller.model.request.UpdateUserModel;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.ClubRole.Role;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {

  User getUserByEmail(String username);

  UserDTO createUser(User user);

  List<UserDTO> createUsers(List<User> users);

  UserDTO updateUser(User user, String userId);

  UserDTO updateUser(User user, String clubId, UpdateUserModel userDetails);

  UserDTO updateUserRoles(User user, Club club, Set<Role> roles);

  void removeUserFromClub(User user, Club club);

  void deleteUser(User user);

  void updateUserLoginTime(String email);

  UserDTO updateUserChildren(User parent, Set<User> children, Club club);

  UserDTO switchClub(User user, Club club);

  UserDTO joinClub(User user, String clubId);
}
