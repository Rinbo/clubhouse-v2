package nu.borjessons.clubhouse.impl.service.impl;

import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
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
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
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
  private static boolean isLeader(ClubUser clubUser) {
    return clubUser.getRoles().stream().anyMatch(roleEntity -> roleEntity.getName() == Role.LEADER);
  }

  private static void updateAddresses(User user, Set<Address> addresses) {
    Set<Address> oldAddresses = user.getAddresses();
    oldAddresses.forEach(user::removeAddress);
    addresses.forEach(user::addAddress);
  }

  private static void updateUserDetails(AdminUpdateUserModel userDetails, User user) {
    user.setFirstName(userDetails.getFirstName());
    user.setLastName(userDetails.getLastName());
    user.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), ClubhouseUtils.DATE_FORMAT));
  }
  private final ClubRepository clubRepository;
  private final ClubUserRepository clubUserRepository;
  private final ClubhouseMappers clubhouseMappers;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  /**
   * When a parent leaves the children stay in that club
   * TODO Parent or admin is prompted if children should be removed also.
   */
  @Override
  @Transactional
  public void removeUserFromClub(UserId userId, String clubId) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId.toString()).orElseThrow();
    clubUserRepository.removeClubUserAnnouncementReferences(clubUser.getId());
    clubUserRepository.delete(clubUser);
  }

  @Override
  @Transactional
  public ClubUserDto updateUser(UserId userId, String clubId, AdminUpdateUserModel userDetails) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId.toString()).orElseThrow();
    User user = clubUser.getUser();
    updateUserDetails(userDetails, user);
    updateAddresses(user, clubhouseMappers.addressModelToAddress(userDetails.getAddresses()));
    updateRoles(clubUser, userDetails.getRoles());
    return new ClubUserDto(userRepository.save(user).getClubUser(clubId).orElseThrow());
  }

  @Override
  @Transactional
  public ClubUserDto getClubUser(String clubId, UserId userId) {
    final ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId.toString()).orElseThrow();
    return new ClubUserDto(clubUser);
  }

  // TODO Implement functionality to notify admins that this user wants to join - Require action to give full permissions
  @Override
  @Transactional
  public ClubUserDto addUserToClub(String clubId, UserId userId, List<UserId> childrenIds) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    User user = userRepository.findByUserId(userId).orElseThrow();
    Set<Role> roles = addChildren(club, user, childrenIds);
    Set<RoleEntity> roleEntities = roleRepository.findByRoleNames(roles.stream().map(Role::name).collect(Collectors.toSet()));
    ClubUser clubUser = new ClubUser();
    roleEntities.forEach(clubUser::addRoleEntity);
    club.addClubUser(clubUser);
    user.addClubUser(clubUser);
    userRepository.save(user);
    return new ClubUserDto(clubUser);
  }

  @Override
  public Collection<ClubUserDto> getLeaders(String clubId) {
    List<ClubUser> clubUsers = clubUserRepository.findByClubId(clubId);
    return clubUsers.stream()
        .filter(ClubUserServiceImpl::isLeader)
        .map(ClubUserDto::new)
        .toList();
  }

  @Override
  public Collection<ClubUserDto> getClubUsers(String clubId) {
    return clubUserRepository.findByClubId(clubId)
        .stream()
        .map(ClubUserDto::new)
        .toList();
  }

  @Override
  public Optional<ClubUserDto> getClubUserByUsername(String clubId, String username) {
    return clubUserRepository.findByClubIdAndUsername(clubId, username).map(ClubUserDto::new).or(Optional::empty);
  }

  @Override
  @Transactional
  public ClubUserDto activateClubChildren(String clubId, UserId userId, List<UserId> childrenIds) {
    Club club = clubRepository.findByClubId(clubId).orElseThrow();
    User user = userRepository.findByUserId(userId).orElseThrow();
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId.toString()).orElseThrow();
    Set<Role> roles = addChildren(club, user, childrenIds);
    Set<RoleEntity> roleNames = roleRepository.findByRoleNames(roles.stream().map(Role::name).collect(Collectors.toSet()));
    roleNames.forEach(clubUser::addRoleEntity);
    userRepository.save(user);
    return new ClubUserDto(clubUser);
  }

  @Override
  public List<ClubUserDto> getAllUsersClubUsers(UserId userId) {
    Objects.requireNonNull(userId, "userId must not be null");

    List<ClubUser> clubUsers = clubUserRepository.findAllByUserId(userId.toString());
    return clubUsers.stream().map(ClubUserDto::new).toList();
  }

  @Override
  @Transactional
  public ClubUserDto removeClubChildren(String clubId, UserId userId, List<UserId> childrenIds) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId.toString()).orElseThrow();
    List<ClubUser> childrenUsers = clubUserRepository.findByClubIdAndUserIds(clubId, childrenIds.stream().map(UserId::toString).toList());
    if (clubUser.getUser().getChildren().stream().allMatch(child -> childrenIds.contains(child.getUserId()))) clubUser.removeParentRole();
    clubUserRepository.deleteAll(childrenUsers);
    return new ClubUserDto(clubUserRepository.save(clubUser));
  }

  private void updateRoles(ClubUser clubUser, Set<Role> roles) {
    final Set<RoleEntity> roleEntities = roleRepository.findByRoleNames(roles.stream()
        .filter(role -> role != Role.SYSTEM_ADMIN)
        .map(Role::toString).collect(Collectors.toSet()));
    clubUser.setRoles(roleEntities);
  }

  /**
   * Adds ClubUser for children unless they have already been added by another parent. If that is the case, just adds role PARENT
   */
  private Set<Role> addChildren(Club club, User parent, List<UserId> childrenIds) {
    Set<Role> roles = EnumSet.of(Role.USER);

    List<UserId> existingClubChildrenIds = clubUserRepository.findByClubIdAndUserIds(club.getClubId(), childrenIds.stream().map(UserId::toString).toList())
        .stream()
        .map(childClubUser -> childClubUser.getUser().getUserId())
        .toList();

    parent.getChildren()
        .stream()
        .filter(child -> childrenIds.contains(child.getUserId()))
        .forEach(child -> {
          roles.add(Role.PARENT);
          if (existingClubChildrenIds.contains(child.getUserId())) return;

          // TODO: Not adding child role for now. Consider removing it all together. Just adds complexity
          ClubUser clubUser = new ClubUser();
          club.addClubUser(clubUser);
          child.addClubUser(clubUser);
        });

    return roles;
  }
}
