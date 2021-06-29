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
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.repository.RoleRepository;
import nu.borjessons.clubhouse.impl.repository.UserRepository;
import nu.borjessons.clubhouse.impl.service.RegistrationService;
import nu.borjessons.clubhouse.impl.util.ClubhouseMappers;

@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {
  private static final Set<Role> OWNER_ROLES = Set.of(Role.USER, Role.OWNER, Role.ADMIN, Role.LEADER);

  private final ClubRepository clubRepository;
  private final ClubhouseMappers clubhouseMappers;
  private final ClubUserRepository clubUserRepository;
  private final RoleRepository roleRepository;
  private final UserRepository userRepository;

  @Transactional
  @Override
  public UserDTO registerChildren(String userId, String clubId, List<CreateChildRequestModel> childDetails) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId).orElseThrow();
    addChildren(clubUser.getUser(), mapChildModelToUser(childDetails));
    getRoleEntities(Set.of(Role.PARENT)).forEach(clubUser::addRoleEntity);
    return UserDTO.create(clubUserRepository.save(clubUser).getUser());
  }

  @Transactional
  @Override
  public UserDTO registerClub(CreateClubModel clubDetails) {
    Club club = clubRepository.save(clubhouseMappers.clubCreationModelToClub(clubDetails));
    User user = createClubUser(club, getRoleEntities(OWNER_ROLES), constructUserEntity(clubDetails.getOwner()));
    return UserDTO.create(user);
  }

  @Transactional
  @Override
  public UserDTO registerClub(CreateClubModel clubDetails, String clubId) {
    Club club = clubRepository.save(clubhouseMappers.clubCreationModelToClub(clubDetails, clubId));
    User user = createClubUser(club, getRoleEntities(OWNER_ROLES), constructUserEntity(clubDetails.getOwner()));
    return UserDTO.create(user);
  }

  @Transactional
  @Override
  public List<UserDTO> registerFamily(FamilyRequestModel familyDetails) {
    List<CreateUserModel> parentsDetails = familyDetails.getParents();
    Set<User> children = mapChildModelToUser(familyDetails.getChildren());
    Club club = clubRepository.findByClubId(familyDetails.getClubId()).orElseThrow();

    Set<Role> roles = new HashSet<>(Collections.singletonList(Role.USER));
    if (!children.isEmpty()) roles.add(Role.PARENT);
    Set<RoleEntity> roleEntities = getRoleEntities(roles);

    List<User> parents = parentsDetails
        .stream()
        .map(this::constructUserEntity)
        .map(parent -> addChildren(parent, children))
        .collect(Collectors.toList());

    parents.forEach(parent -> createClubUser(club, roleEntities, parent));

    return parents.stream().map(UserDTO::create).collect(Collectors.toList());
  }

  @Transactional
  @Override
  public UserDTO registerUser(CreateUserModel userDetails) {
    Set<User> children = mapChildModelToUser(userDetails.getChildren());
    Club club = clubRepository.findByClubId(userDetails.getClubId()).orElseThrow();

    User user = constructUserEntity(userDetails);
    addChildren(user, children);

    Set<Role> roles = new HashSet<>(Collections.singletonList(Role.USER));
    if (!children.isEmpty()) roles.add(Role.PARENT);

    return UserDTO.create(createClubUser(club, getRoleEntities(roles), user));
  }

  private Set<User> mapChildModelToUser(List<CreateChildRequestModel> childrenDetails) {
    return childrenDetails.stream()
        .map(clubhouseMappers::childCreationModelToUser)
        .collect(Collectors.toSet());
  }

  private User addChildren(User parent, Set<User> children) {
    children.forEach(parent::addChild);
    return parent;
  }

  private User createClubUser(Club club, Set<RoleEntity> roleEntities, User user) {
    ClubUser clubUser = new ClubUser();
    user.addClubUser(clubUser);
    club.addClubUser(clubUser);
    clubUser.setRoles(roleEntities);
    return userRepository.save(user);
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
