package nu.borjessons.clubhouse.impl.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;

public interface UserService extends UserDetailsService {

  UserDTO createUser(User user);

  List<UserDTO> createUsers(List<User> users);

  UserDTO getUserByUserName(String username);

  UserDTO getById(long id);

  void deleteUser(long id);

  User getUserByEmail(String username);

  UserDTO updateUser(long id, UpdateUserModel userDetails);

  void updateUserLoginTime(String email);

  List<ClubDTO> getMyClubs(String userId);

  UserDTO updateChild(String childId, String parentId, UpdateUserModel userDetails);
}
