package nu.borjessons.clubhouse.impl.service;

import nu.borjessons.clubhouse.impl.controller.model.request.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.controller.model.request.UpdateUserModel;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Set;

public interface UserService extends UserDetailsService {

  User getUserByEmail(String username);

  UserDTO createUser(User user);

  List<UserDTO> createUsers(List<User> users);

  UserDTO updateUser(User user, String userId);

  UserDTO updateUser(User user, Club club, UpdateUserModel userDetails);

  UserDTO updateUser(User user, Club club, AdminUpdateUserModel userDetails);

  void removeUserFromClub(User user, Club club);

  void deleteUser(User user);

  void updateUserLoginTime(String email);

  UserDTO updateUserChildren(User parent, Set<User> children, Club club);

  UserDTO switchClub(User user, Club club);

  UserDTO joinClub(User user, String clubId);
}
