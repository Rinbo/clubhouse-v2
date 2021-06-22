package nu.borjessons.clubhouse.impl.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;

public interface UserService extends UserDetailsService {

  UserDTO createUser(User user);

  List<UserDTO> createUsers(List<User> users);

  UserDTO getById(long id);

  void deleteUser(long id);

  User getUserByEmail(String username);

  UserDTO updateUser(User user, UpdateUserModel userDetails);

  void updateUserLoginTime(String email);
}
