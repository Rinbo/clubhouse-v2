package nu.borjessons.clubhouse.service.impl;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.UpdateUserModel;
import nu.borjessons.clubhouse.data.Address;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.ClubRole;
import nu.borjessons.clubhouse.data.ClubRole.Role;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.UserDTO;
import nu.borjessons.clubhouse.repository.AddressRepository;
import nu.borjessons.clubhouse.repository.UserRepository;
import nu.borjessons.clubhouse.service.ClubService;
import nu.borjessons.clubhouse.service.TeamService;
import nu.borjessons.clubhouse.service.UserService;
import nu.borjessons.clubhouse.util.ClubhouseMappers;
import nu.borjessons.clubhouse.util.ClubhouseUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final ClubService clubService;
  private final AddressRepository addressRepository;
  private final TeamService teamService;
  private final ClubhouseMappers clubhouseMappers;

  @Override
  public UserDetails loadUserByUsername(String username) {
    Optional<User> user = userRepository.findByEmail(username);
    return getOrThrowUNFE(user, username);
  }

  @Override
  public User getUserByEmail(String email) {
    Optional<User> user = userRepository.findByEmail(email);
    return getOrThrowUNFE(user, email);
  }

  @Override
  public UserDTO createUser(User user) {
    return new UserDTO(userRepository.save(user), user.getActiveClubId());
  }

  @Override
  public List<UserDTO> createUsers(List<User> users) {
    return userRepository.saveAll(users).stream()
        .map(user -> new UserDTO(user, user.getActiveClubId()))
        .collect(Collectors.toList());
  }

  @Override
  public UserDTO updateUser(User user, String clubId) {
    return new UserDTO(userRepository.save(user), clubId);
  }

  @Override
  public UserDTO updateUser(User user, String clubId, UpdateUserModel userDetails) {
    user.setFirstName(userDetails.getFirstName());
    user.setLastName(userDetails.getLastName());
    user.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), ClubhouseUtils.DATE_FORMAT));

    Set<Address> addresses = clubhouseMappers.addressModelToAddress(userDetails.getAddresses());

    Set<Address> oldAddresses = user.getAddresses();
    oldAddresses.forEach(address -> address.setUser(null));
    user.setAddresses(new HashSet<>());
    addressRepository.deleteAll(oldAddresses);
    addresses.forEach(user::addAddress);

    return new UserDTO(userRepository.save(user), clubId);
  }

  @Override
  public UserDTO updateUserRoles(User user, Club club, Set<Role> roles) {
    String clubId = club.getClubId();
    Set<ClubRole> allRoles = user.getRoles();
    Set<ClubRole> rolesInClub =
        allRoles.stream()
            .filter(clubRole -> clubRole.getClub().getClubId().equals(clubId))
            .collect(Collectors.toSet());

    rolesInClub.forEach(user::removeClubRole);
    clubhouseMappers.mapClubRoles(roles, user, club);

    return new UserDTO(userRepository.save(user), clubId);
  }

  @Override
  @Transactional
  public void removeUserFromClub(User user, Club club) {
    Set<User> children = user.getChildren();
    if (!children.isEmpty()) {
      Set<User> otherParentsInThisClub =
          children.stream()
              .map(User::getParents)
              .flatMap(Set::stream)
              .filter(
                  parent -> {
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
  @Transactional
  public void deleteUser(User user) {
    Set<User> children = user.getChildren();
    user.setChildren(new HashSet<>());
    children.forEach(
        child -> {
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
  public void updateUserLoginTime(String email) {
    Optional<User> maybeUser = userRepository.findByEmail(email);
    User user = getOrThrowUNFE(maybeUser, email);
    user.setLastLoginTime(LocalDateTime.now());
    userRepository.save(user);
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

    savedChildren.forEach(
        child -> {
          if (child.getParents().isEmpty()) userRepository.delete(child);
        });

    if (!parent.getChildren().isEmpty()) {
      ClubRole clubRole = new ClubRole(Role.PARENT, parent, club);
      parent.addClubRole(clubRole);
      club.addClubRole(clubRole);
    } else {
      parent.getRoles().stream()
          .filter(
              clubRole -> clubRole.getClub().equals(club) && clubRole.getRole().equals(Role.PARENT))
          .collect(Collectors.toSet())
          .forEach(ClubRole::doOrphan);
    }

    User savedParent = userRepository.save(parent);

    return new UserDTO(savedParent, club.getClubId());
  }

  @Override
  public UserDTO switchClub(User user, Club club) {
    user.setActiveClubId(club.getClubId());
    return new UserDTO(userRepository.save(user), club.getClubId());
  }

  @Override
  public UserDTO joinClub(User user, String clubId) {
    Club club = clubService.getClubByClubId(clubId);
    Set<Role> roles = new HashSet<>(List.of(Role.USER));
    if (!user.getChildren().isEmpty()) roles.add(Role.PARENT);
    clubhouseMappers.mapClubRoles(roles, user, club);
    user.setActiveClubId(clubId);
    return new UserDTO(userRepository.save(user), clubId);
  }

  @Override
  public Set<User> getUsers(String clubId, Set<String> userIds) {
    return null;
  }

  private static User getOrThrowUNFE(Optional<User> user, String email) {
    return user.orElseThrow(
        () -> new UsernameNotFoundException(String.format("User %s could not be found", email)));
  }
}
