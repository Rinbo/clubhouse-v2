package nu.borjessons.clubhouse.impl.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;

public interface ClubUserService {
  ClubUserDto activateClubChildren(String clubId, UserId userId, List<UserId> childrenIds);

  ClubUserDto addUserToClub(String clubId, UserId userId, List<UserId> childrenIds);

  List<ClubUserDto> getAllUsersClubUsers(UserId userId);

  ClubUserDto getClubUser(String clubId, UserId userId);

  Optional<ClubUserDto> getClubUserByUsername(String clubId, String username);

  Collection<ClubUserDto> getClubUsers(String clubId);

  Collection<BaseUserRecord> getClubUsersBasic(String clubId);

  Collection<ClubUserDto> getLeaders(String clubId);

  ClubUserDto removeClubChildren(String clubId, UserId userId, List<UserId> childrenIds);

  void removeUserFromClub(UserId userId, String clubId);

  ClubUserDto updateUser(UserId userId, String clubId, AdminUpdateUserModel userDetails);
}
