package nu.borjessons.clubhouse.impl.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Address;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubRole;
import nu.borjessons.clubhouse.impl.data.ClubRole.RoleTemp;
import nu.borjessons.clubhouse.impl.data.Team;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;
import nu.borjessons.clubhouse.impl.repository.AddressRepository;
import nu.borjessons.clubhouse.impl.repository.UserRepository;
import nu.borjessons.clubhouse.impl.service.ClubService;
import nu.borjessons.clubhouse.impl.service.TeamService;
import nu.borjessons.clubhouse.impl.service.UserService;
import nu.borjessons.clubhouse.impl.util.ClubhouseMappers;
import nu.borjessons.clubhouse.impl.util.ClubhouseUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final AddressRepository addressRepository;
  private final ClubService clubService;
  private final ClubhouseMappers clubhouseMappers;
  private final TeamService teamService;
  private final UserRepository userRepository;

  @Override
  public UserDTO createUser(User user) {
    return UserDTO.create(userRepository.save(user));
  }

  @Override
  public List<UserDTO> createUsers(List<User> users) {
    return userRepository.saveAll(users)
        .stream()
        .map(UserDTO::create)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void deleteUser(User user) {
    Set<User> children = user.getChildren();
    user.setChildren(new HashSet<>());
    children.forEach(child -> {
      child.removeParent(user);
      if (child.getParents().isEmpty()) {
        userRepository.delete(child);
      } else {
        userRepository.save(child);
      }
    });
    userRepository.delete(user);
  }

  @Override
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email).orElseThrow();
  }

  @Override
  @Transactional
  public void removeUserFromClub(User user, Club club) {
    Set<User> children = user.getChildren();
    if (!children.isEmpty()) {
      Set<User> otherParentsInThisClub = children
          .stream()
          .map(User::getParents)
          .flatMap(Set::stream)
          .filter(parent -> {
            Set<Club> clubs = parent.getClubs();
            return clubs.contains(club) && !parent.getUserId().equals(user.getUserId());
          })
          .collect(Collectors.toSet());

      if (otherParentsInThisClub.isEmpty()) {
        teamService.removeUsersFromAllTeams(children, club);
      }
    }

    teamService.removeUsersFromAllTeams(new HashSet<>(List.of(user)), club);

    Set<ClubRole> clubRolesForRemoval =
        user.getRoles().stream()
            .filter(clubRole -> clubRole.getClub().equals(club))
            .collect(Collectors.toSet());
    clubRolesForRemoval.forEach(ClubRole::doOrphan);
    // Note: User::activeClubId is not rotated so the next call will mean no roles are set and users
    // should be redirected to "choose club" interface
    userRepository.save(user);
  }

  @Override
  public UserDTO updateUser(User user) {
    return UserDTO.create(userRepository.save(user));
  }

  @Override
  @Transactional
  public UserDTO updateUser(User user, UpdateUserModel userDetails) {
    user.setFirstName(userDetails.getFirstName());
    user.setLastName(userDetails.getLastName());
    user.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), ClubhouseUtils.DATE_FORMAT));

    Set<Address> addresses = clubhouseMappers.addressModelToAddress(userDetails.getAddresses());
    Set<Address> oldAddresses = user.getAddresses();
    oldAddresses.forEach(user::removeAddress);
    addressRepository.deleteAll(oldAddresses);
    addresses.forEach(user::addAddress);

    return UserDTO.create(userRepository.save(user));
  }

  @Override
  @Transactional
  public UserDTO updateUser(User user, Club club, AdminUpdateUserModel userDetails) {
    updateUserRoles(user, club, userDetails.getRoles());
    return updateUser(user, userDetails);
  }

  @Override
  @Transactional
  public UserDTO updateUserChildren(User parent, Set<User> children, Club club) {
    Set<User> removedChildren = parent.getChildren();
    parent.setChildren(new HashSet<>());
    removedChildren.forEach(child -> child.removeParent(parent));

    children.forEach(child -> child.addParent(parent));
    children.addAll(removedChildren);
    List<User> savedChildren = userRepository.saveAll(children);

    savedChildren.forEach(child -> {
      if (child.getParents().isEmpty())
        userRepository.delete(child);
    });

    if (!parent.getChildren().isEmpty()) {
      ClubRole clubRole = new ClubRole(RoleTemp.PARENT, parent, club);
      parent.addClubRole(clubRole);
      club.addClubRole(clubRole);
    } else {
      parent.getRoles()
          .stream()
          .filter(clubRole -> clubRole.getClub().equals(club) && clubRole.getRole().equals(RoleTemp.PARENT))
          .collect(Collectors.toSet())
          .forEach(ClubRole::doOrphan);
    }

    return UserDTO.create(userRepository.save(parent));
  }

  @Override
  public void updateUserLoginTime(String email) {
    User user = userRepository.findByEmail(email).orElseThrow();
    user.setLastLoginTime(LocalDateTime.now());
    userRepository.save(user);
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    return userRepository.findByEmail(username).orElseThrow();
  }

  private void updateUserRoles(User user, Club club, Set<RoleTemp> roles) {
    String clubId = club.getClubId();
    Set<ClubRole> allRoles = user.getRoles();
    Set<ClubRole> rolesInClub = allRoles
        .stream()
        .filter(clubRole -> clubRole.getClub().getClubId().equals(clubId))
        .collect(Collectors.toSet());

    rolesInClub.forEach(user::removeClubRole);
    clubhouseMappers.mapClubRoles(roles, user, club);

    if (!roles.contains(RoleTemp.LEADER)) {
      Set<Team> teams = club.getTeams();
      teams.forEach(team -> team.removeLeader(user));
      clubService.saveClub(club);
    }
  }
}
