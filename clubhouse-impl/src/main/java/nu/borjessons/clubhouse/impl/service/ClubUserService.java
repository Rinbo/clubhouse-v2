package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;

public interface ClubUserService {
  void removeUserFromClub(String userId, String clubId);

  ClubUserDTO updateUser(String userId, String clubId, AdminUpdateUserModel userDetails);

  ClubUserDTO getClubUser(String clubId, String userId);

  ClubUserDTO addUserToClub(String clubId, String userId, List<String> childrenIds);

  Collection<ClubUserDTO> getLeaders(String clubId);

  Collection<ClubUserDTO> getClubUsers(String clubId);

  Optional<ClubUserDTO> getClubUserByUsername(String clubId, String username);

  ClubUserDTO activateClubChildren(String clubId, String userId, List<String> childrenIds);

  List<ClubUserDTO> getAllUsersClubUsers(String userId);

  ClubUserDTO removeClubChildren(String clubId, String userId, List<String> childrenIds);
}
