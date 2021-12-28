package nu.borjessons.clubhouse.impl.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.ClubDto;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;

public interface UserService extends UserDetailsService {

  UserDto createUser(User user);

  List<UserDto> createUsers(List<User> users);

  UserDto getUserByUserName(String username);

  UserDto getById(long id);

  void deleteUser(long id);

  User getUserByEmail(String username);

  UserDto updateUser(long id, UpdateUserModel userDetails);

  void updateUserLoginTime(String email);

  List<ClubDto> getMyClubs(UserId userId);

  UserDto updateChild(UserId childId, UserId parentId, UpdateUserModel userDetails);

  void addParentToChild(UserId originalParentId, UserId childId, UserId newParentId);
}
