package nu.borjessons.clubhouse.impl.service.impl;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.repository.RoleRepository;
import nu.borjessons.clubhouse.impl.repository.UserRepository;
import nu.borjessons.clubhouse.impl.service.ClubUserService;
import nu.borjessons.clubhouse.impl.util.ClubhouseMappers;
import nu.borjessons.clubhouse.impl.util.ClubhouseUtils;

@Service
@RequiredArgsConstructor
public class ClubUserServiceImpl implements ClubUserService {
  private static void updateUserDetails(AdminUpdateUserModel userDetails, User user) {
    user.setFirstName(userDetails.getFirstName());
    user.setLastName(userDetails.getLastName());
    user.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), ClubhouseUtils.DATE_FORMAT));
  }

  private static void updateAddresses(User user, Set<Address> addresses) {
    Set<Address> oldAddresses = user.getAddresses();
    oldAddresses.forEach(user::removeAddress);
    addresses.forEach(user::addAddress);
  }

  private static boolean isLeader(ClubUser clubUser) {
    return clubUser.getRoles().stream().anyMatch(roleEntity -> roleEntity.getName() == Role.LEADER);
  }

  private final ClubRepository clubRepository;
  private final ClubUserRepository clubUserRepository;
  private final ClubhouseMappers clubhouseMappers;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  /**
   * When a parent leaves the children stay in that club
   * Parent or admin is prompted if children should be removed also.
   */
  @Override
  @Transactional
  public void removeUserFromClub(String userId, String clubId) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId).orElseThrow();
    clubUserRepository.delete(clubUser);
  }

  @Override
  @Transactional
  public ClubUserDTO updateUser(String userId, String clubId, AdminUpdateUserModel userDetails) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId).orElseThrow();
    User user = clubUser.getUser();
    updateUserDetails(userDetails, user);
    updateAddresses(user, clubhouseMappers.addressModelToAddress(userDetails.getAddresses()));
    updateRoles(clubUser, userDetails.getRoles());
    return new ClubUserDTO(userRepository.save(user).getClubUser(clubId).orElseThrow());
  }

  @Override
  @Transactional
  public ClubUserDTO addExistingChildrenToUser(String userId, String clubId, List<String> childrenIds) {
    User parent = userRepository.findByUserId(userId).orElseThrow();
    List<ClubUser> childrenClubUsers = clubUserRepository.findByClubIdAndUserIds(clubId, childrenIds);
    childrenClubUsers.stream()
        .map(ClubUser::getUser)
        .filter(User::isManagedAccount)
        .forEach(child -> child.addParent(parent));
    return new ClubUserDTO(userRepository.save(parent).getClubUser(clubId).orElseThrow());
  }

  @Override
  @Transactional
  public ClubUserDTO getClubUser(String clubId, String userId) {
    final ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId).orElseThrow();
    return new ClubUserDTO(clubUser);
  }

  // TODO Implement functionality to notify admins that this user wants to join - Require action to give full permissions
  // TODO boolean to port children as well?
  @Override
  @Transactional
  public ClubUserDTO addUserToClub(String clubId, String userId) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    User user = userRepository.findByUserId(userId).orElseThrow();
    Set<RoleEntity> roleEntities = roleRepository.findByRoleNames(Set.of(Role.USER.name()));
    ClubUser clubUser = new ClubUser();
    roleEntities.forEach(clubUser::addRoleEntity);
    club.addClubUser(clubUser);
    user.addClubUser(clubUser);
    userRepository.save(user);
    return new ClubUserDTO(clubUser);
  }

  @Override
  public Collection<ClubUserDTO> getLeaders(String clubId) {
    List<ClubUser> clubUsers = clubUserRepository.findByClubId(clubId);
    return clubUsers.stream()
        .filter(ClubUserServiceImpl::isLeader)
        .map(ClubUserDTO::new)
        .collect(Collectors.toList());
  }

  @Override
  public Collection<ClubUserDTO> getClubUsers(String clubId) {
    return clubUserRepository.findByClubId(clubId)
        .stream()
        .map(ClubUserDTO::new)
        .collect(Collectors.toList());
  }

  @Override
  public Optional<ClubUserDTO> getClubUserByUsername(String clubId, String username) {
    return clubUserRepository.findByClubIdAndUsername(clubId, username).map(ClubUserDTO::new).or(Optional::empty);
  }

  private void updateRoles(ClubUser clubUser, Set<Role> roles) {
    final Set<RoleEntity> roleEntities = roleRepository.findByRoleNames(roles.stream()
        .filter(role -> role != Role.SYSTEM_ADMIN)
        .map(Role::toString).collect(Collectors.toSet()));
    clubUser.setRoles(roleEntities);
  }
}
