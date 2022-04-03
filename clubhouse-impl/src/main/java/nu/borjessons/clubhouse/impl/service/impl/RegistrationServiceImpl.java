package nu.borjessons.clubhouse.impl.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.repository.RoleRepository;
import nu.borjessons.clubhouse.impl.repository.UserRepository;
import nu.borjessons.clubhouse.impl.service.ImageService;
import nu.borjessons.clubhouse.impl.service.RegistrationService;
import nu.borjessons.clubhouse.impl.util.ClubhouseMappers;

// TODO eventually remove the mappers and create converter functions in controller that does the mapping to internal DTO?
@RequiredArgsConstructor
@Service
@Transactional
public class RegistrationServiceImpl implements RegistrationService {
  private static final Set<Role> OWNER_ROLES = Set.of(Role.USER, Role.OWNER, Role.ADMIN, Role.LEADER);

  private final ClubRepository clubRepository;
  private final ClubhouseMappers clubhouseMappers;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final ImageService imageService;

  @Override
  public UserDto registerClubChildren(UserId userId, String clubId, List<CreateChildRequestModel> childDetails) {
    User parent = userRepository.findByUserId(userId).orElseThrow();
    ClubUser clubUser = parent.getClubUser(clubId).orElseThrow();
    Set<User> children = mapChildModelToUser(childDetails);

    if (!children.isEmpty()) getRoleEntities(Set.of(Role.PARENT)).forEach(clubUser::addRoleEntity);

    addChildrenToParent(parent, children);
    children.forEach(child -> addClubUser(clubUser.getClub(), getRoleEntities(Set.of(Role.CHILD)), child));

    return UserDto.create(userRepository.save(parent));
  }

  @Override
  public UserDto registerClub(CreateClubModel clubDetails) {
    Club club = clubRepository.save(clubhouseMappers.clubCreationModelToClub(clubDetails));
    User user = userRepository.save(addClubUser(club, getRoleEntities(OWNER_ROLES), constructUserEntity(clubDetails.getOwner())));
    imageService.createClubRootImageFolder(club.getClubId());
    return UserDto.create(user);
  }

  @Override
  public UserDto registerClub(CreateClubModel clubDetails, String clubId) {
    Club club = clubRepository.save(clubhouseMappers.clubCreationModelToClub(clubDetails, clubId));
    User user = userRepository.save(addClubUser(club, getRoleEntities(OWNER_ROLES), constructUserEntity(clubDetails.getOwner())));
    imageService.createClubRootImageFolder(club.getClubId());
    return UserDto.create(user);
  }

  @Override
  public List<UserDto> registerFamily(FamilyRequestModel familyDetails) {
    List<CreateUserModel> parentsDetails = familyDetails.getParents();
    Set<User> children = mapChildModelToUser(familyDetails.getChildren());
    Club club = clubRepository.findByClubId(familyDetails.getClubId()).orElseThrow();

    Set<Role> roles = new HashSet<>(Collections.singletonList(Role.USER));
    if (!children.isEmpty()) roles.add(Role.PARENT);
    Set<RoleEntity> parentRoleEntities = getRoleEntities(roles);

    List<User> parents = parentsDetails
        .stream()
        .map(this::constructUserEntity)
        .map(parent -> addChildrenToParent(parent, children))
        .map(parent -> addClubUser(club, parentRoleEntities, parent))
        .toList();

    children.forEach(child -> addClubUser(club, getRoleEntities(Set.of(Role.CHILD)), child));

    return userRepository.saveAll(parents).stream().map(UserDto::create).toList();
  }

  @Override
  public UserDto registerUser(CreateUserModel userDetails) {
    Club club = clubRepository.findByClubId(userDetails.getClubId()).orElseThrow();
    Set<User> children = mapChildModelToUser(userDetails.getChildren());

    User user = constructUserEntity(userDetails);
    children.forEach(child -> addClubUser(club, getRoleEntities(Set.of(Role.CHILD)), child));
    addChildrenToParent(user, children);

    Set<Role> roles = new HashSet<>(Collections.singletonList(Role.USER));
    if (!children.isEmpty()) roles.add(Role.PARENT);

    addClubUser(club, getRoleEntities(roles), user);
    return UserDto.create(userRepository.save(user));
  }

  @Transactional
  @Override
  public UserDto registerChild(UserId parentId, CreateChildRequestModel childModel) {
    User parent = userRepository.findByUserId(parentId).orElseThrow();
    User child = clubhouseMappers.childCreationModelToUser(childModel);
    parent.addChild(child);
    User savedParent = userRepository.save(parent);
    return UserDto.create(savedParent);
  }

  @Transactional
  @Override
  public UserDto unregisterChild(UserId childId, UserId parentId) {
    User parent = userRepository.findByUserId(parentId).orElseThrow();
    User child = parent.getChildren().stream().filter(c -> c.getUserId().equals(childId)).findFirst().orElseThrow();
    parent.removeChild(child);
    userRepository.delete(child);
    return UserDto.create(parent);
  }

  @Override
  public UserDto registerUser(CreateUserModel userDetails, UserId userId) {
    Club club = clubRepository.findByClubId(userDetails.getClubId()).orElseThrow();

    User user = clubhouseMappers.userCreationModelToUser(userDetails, userId);
    Set<Role> roles = new HashSet<>(Collections.singletonList(Role.USER));

    addClubUser(club, getRoleEntities(roles), user);
    return UserDto.create(userRepository.save(user));
  }

  private Set<User> mapChildModelToUser(List<CreateChildRequestModel> childrenDetails) {
    return childrenDetails.stream()
        .map(clubhouseMappers::childCreationModelToUser)
        .collect(Collectors.toSet());
  }

  private User addChildrenToParent(User parent, Set<User> children) {
    children.forEach(parent::addChild);
    return parent;
  }

  private User addClubUser(Club club, Set<RoleEntity> roleEntities, User user) {
    ClubUser clubUser = new ClubUser();
    user.addClubUser(clubUser);
    club.addClubUser(clubUser);
    clubUser.setRoles(roleEntities);
    return user;
  }

  private Set<RoleEntity> getRoleEntities(Set<Role> myRoles) {
    return roleRepository.findByRoleNames(myRoles.stream().map(Role::toString).collect(Collectors.toSet()));
  }

  private User constructUserEntity(CreateUserModel userDetails) {
    User user = clubhouseMappers.userCreationModelToUser(userDetails);
    Set<Address> addresses = clubhouseMappers.addressModelToAddress(userDetails.getAddresses());
    addresses.forEach(user::addAddress);
    return user;
  }
}
