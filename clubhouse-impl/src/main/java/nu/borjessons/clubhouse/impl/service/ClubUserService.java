package nu.borjessons.clubhouse.impl.service;

import java.util.Set;

import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;

public interface ClubUserService {
  void removeUserFromClub(String userId, String clubId);

  UserDTO updateUser(String userId, String clubId, AdminUpdateUserModel userDetails);

  User addExistingChildrenToUser(String userId, String clubId, Set<String> childrenIds);

  UserDTO getClubUser(String clubId, String userId);
}
