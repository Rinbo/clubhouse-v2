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
@Transactional
public class ClubUserServiceImpl implements ClubUserService {
  private final ClubUserRepository clubUserRepository;
  private final ClubhouseMappers clubhouseMappers;
  private final TeamService teamService;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  /**
   * When a parent leaves the children say in that club
   * Parent or admin is prompted if children should be removed also.
   */
  @Override
  public void removeUserFromClub(String userId, String clubId) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId).orElseThrow();
    clubUser.doOrphan(); // Do I need this?
    clubUserRepository.delete(clubUser);
  }

  @Override
  public UserDTO updateUser(String userId, String clubId, AdminUpdateUserModel userDetails) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId).orElseThrow();
    User user = clubUser.getUser();
    updateUserDetails(userDetails, user);
    updateAddresses(user, clubhouseMappers.addressModelToAddress(userDetails.getAddresses()));
    updateRoles(clubUser, userDetails.getRoles());
    return UserDTO.create(userRepository.save(user));
  }

  @Override
  public User addExistingChildrenToUser(String userId, String clubId, Set<String> childrenIds) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId).orElseThrow();
    User user = clubUser.getUser();
    Club club = clubUser.getClub();
    Set<User> children = club.getManagedUsers().stream().filter(child -> childrenIds.contains(child.getUserId())).collect(Collectors.toSet());
    children.forEach(child -> child.addParent(user));
    return clubUserRepository.save(clubUser).getUser();
  }

  @Override
  public UserDTO getClubUser(String clubId, String userId) {
    final ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId).orElseThrow();
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
