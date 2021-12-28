package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;

public interface ClubUserService {
  void removeUserFromClub(UserId userId, String clubId);

  ClubUserDto updateUser(UserId userId, String clubId, AdminUpdateUserModel userDetails);

  ClubUserDto getClubUser(String clubId, UserId userId);

  ClubUserDto addUserToClub(String clubId, UserId userId, List<UserId> childrenIds);

  Collection<ClubUserDto> getLeaders(String clubId);

  Collection<ClubUserDto> getClubUsers(String clubId);

  Optional<ClubUserDto> getClubUserByUsername(String clubId, String username);

  ClubUserDto activateClubChildren(String clubId, UserId userId, List<UserId> childrenIds);

  List<ClubUserDto> getAllUsersClubUsers(UserId userId);

  ClubUserDto removeClubChildren(String clubId, UserId userId, List<UserId> childrenIds);
}
