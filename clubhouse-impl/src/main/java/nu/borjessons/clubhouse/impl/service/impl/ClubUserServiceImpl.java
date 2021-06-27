package nu.borjessons.clubhouse.impl.service.impl;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Address;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.RoleEntity;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.repository.RoleRepository;
import nu.borjessons.clubhouse.impl.repository.UserRepository;
import nu.borjessons.clubhouse.impl.service.ClubUserService;
import nu.borjessons.clubhouse.impl.service.TeamService;
import nu.borjessons.clubhouse.impl.util.ClubhouseMappers;
import nu.borjessons.clubhouse.impl.util.ClubhouseUtils;

@Service
@RequiredArgsConstructor
public class ClubUserServiceImpl implements ClubUserService {
  private final ClubUserRepository clubUserRepository;
  private final ClubhouseMappers clubhouseMappers;
  private final TeamService teamService;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  // TODO test this rigorously since the mapping relationship is uncertain
  // If user is a parent will it not mean some children can be left in a team of this club?
  // What if this method is called on a managed user? Then it will throw a NoSuchElementException
  // Here we are getting into the reason why it is such a bad idea to have team members decoupled from
  // the club relationship. I will be playing catchup forever. Can the teams relationship be tied to
  // club user instead? Not in the current implementation because managed users (children) do not have
  // a club user. Put children relation in ClubUser instead? No
  @Override
  @Transactional
  public void removeUserFromClub(String userId, String clubId) {
    ClubUser clubUser = clubUserRepository.findByUserIdAndClubId(clubId, userId).orElseThrow();
    User user = clubUser.getUser();
    // Get users children and call teamService::removeUsersFromAllTeams ?
    // What if the other parent is still in the club?
    // I probably have to write a specific method just for this case
    teamService.removeUsersFromAllTeams(user.getChildren(), clubUser.getClub());
    clubUser.doOrphan();
    clubUserRepository.delete(clubUser);
  }

  @Override
  @Transactional
  public UserDTO updateUser(String userId, String clubId, AdminUpdateUserModel userDetails) {
    ClubUser clubUser = clubUserRepository.findByUserIdAndClubId(clubId, userId).orElseThrow();
    User user = clubUser.getUser();
    updateUserDetails(userDetails, user);
    updateAddresses(user, clubhouseMappers.addressModelToAddress(userDetails.getAddresses()));
    updateRoles(clubUser, userDetails.getRoles());
    return UserDTO.create(userRepository.save(user));
  }

  @Override
  @Transactional
  public User addExistingChildrenToUser(String userId, String clubId, Set<String> childrenIds) {
    ClubUser clubUser = clubUserRepository.findByUserIdAndClubId(clubId, userId).orElseThrow();
    User user = clubUser.getUser();
    Club club = clubUser.getClub();
    Set<User> children = club.getManagedUsers().stream().filter(child -> childrenIds.contains(child.getUserId())).collect(Collectors.toSet());
    children.forEach(child -> child.addParent(user));
    return clubUserRepository.save(clubUser).getUser();
  }

  @Override
  public UserDTO getClubUser(String clubId, String userId) {
    final ClubUser clubUser = clubUserRepository.findByUserIdAndClubId(clubId, userId).orElseThrow();
    return UserDTO.create(clubUser.getUser());
  }

  private void updateRoles(ClubUser clubUser, Set<Role> roles) {
    final Set<RoleEntity> roleEntities = roleRepository.findByRoleNames(roles.stream().map(Role::toString).collect(Collectors.toSet()));
    clubUser.setRoles(roleEntities);
  }

  private void updateUserDetails(AdminUpdateUserModel userDetails, User user) {
    user.setFirstName(userDetails.getFirstName());
    user.setLastName(userDetails.getLastName());
    user.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), ClubhouseUtils.DATE_FORMAT));
  }

  private void updateAddresses(User user, Set<Address> addresses) {
    Set<Address> oldAddresses = user.getAddresses();
    oldAddresses.forEach(user::removeAddress);
    addresses.forEach(user::addAddress);
  }
}
