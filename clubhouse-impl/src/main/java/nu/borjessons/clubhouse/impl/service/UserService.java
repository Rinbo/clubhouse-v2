package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;

public interface UserService extends UserDetailsService {

  void addParentToChild(UserId originalParentId, UserId childId, UserId newParentId);

  UserDto createUser(User user);

  List<UserDto> createUsers(List<User> users);

  void deleteUser(long id);

  UserDto getById(long id);

  Collection<BaseUserRecord> getChildren(long id);

  List<ClubRecord> getMyClubs(UserId userId);

  User getUserByEmail(String username);

  UserDto getUserByUsername(String username);

  UserDto updateChild(UserId childId, UserId parentId, UpdateUserModel userDetails);

  UserDto updateUser(long id, UpdateUserModel userDetails);

  UserDto updateUserLoginTime(String email);
}
