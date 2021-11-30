package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;

public interface ClubUserService {
  void removeUserFromClub(String userId, String clubId);

  ClubUserDto updateUser(String userId, String clubId, AdminUpdateUserModel userDetails);

  ClubUserDto getClubUser(String clubId, String userId);

  ClubUserDto addUserToClub(String clubId, String userId, List<String> childrenIds);

  Collection<ClubUserDto> getLeaders(String clubId);

  Collection<ClubUserDto> getClubUsers(String clubId);

  Optional<ClubUserDto> getClubUserByUsername(String clubId, String username);

  ClubUserDto activateClubChildren(String clubId, String userId, List<String> childrenIds);

  List<ClubUserDto> getAllUsersClubUsers(String userId);

  ClubUserDto removeClubChildren(String clubId, String userId, List<String> childrenIds);
}
