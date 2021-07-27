package nu.borjessons.clubhouse.impl.service;

import java.util.List;

import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;

public interface ClubUserService {
  void removeUserFromClub(String userId, String clubId);

  ClubUserDTO updateUser(String userId, String clubId, AdminUpdateUserModel userDetails);

  ClubUserDTO addExistingChildrenToUser(String userId, String clubId, List<String> childrenIds);

  ClubUserDTO getClubUser(String clubId, String userId);

  ClubUserDTO addUserToClub(String clubId, String userId);
}
