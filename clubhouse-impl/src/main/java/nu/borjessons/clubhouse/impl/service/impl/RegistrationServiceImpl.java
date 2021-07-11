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
import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;
import nu.borjessons.clubhouse.impl.dto.Role;
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
  public ClubUserDTO registerChildren(String userId, String clubId, List<CreateChildRequestModel> childDetails) {
    ClubUser clubUser = clubUserRepository.findByClubIdAndUserId(clubId, userId).orElseThrow();
    Set<User> children = mapChildModelToUser(childDetails);
    children.forEach(child -> createClubUser(clubUser.getClub(), getRoleEntities(Set.of(Role.CHILD)), child));
    addChildrenToParent(clubUser.getUser(), children);
    getRoleEntities(Set.of(Role.PARENT)).forEach(clubUser::addRoleEntity);
    return new ClubUserDTO(clubUserRepository.save(clubUser));
  }

  @Transactional
  @Override
  public ClubUserDTO registerClub(CreateClubModel clubDetails) {
    Club club = clubRepository.save(clubhouseMappers.clubCreationModelToClub(clubDetails));
    ClubUser clubUser = createClubUser(club, getRoleEntities(OWNER_ROLES), constructUserEntity(clubDetails.getOwner()));
    return new ClubUserDTO(clubUser);
  }

  @Transactional
  @Override
  public ClubUserDTO registerClub(CreateClubModel clubDetails, String clubId) {
    Club club = clubRepository.save(clubhouseMappers.clubCreationModelToClub(clubDetails, clubId));
    ClubUser clubUser = createClubUser(club, getRoleEntities(OWNER_ROLES), constructUserEntity(clubDetails.getOwner()));
    return new ClubUserDTO(clubUser);
  }

  @Transactional
  @Override
  public List<ClubUserDTO> registerFamily(FamilyRequestModel familyDetails) {
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
        .collect(Collectors.toList());

    List<ClubUser> parentClubUsers = parents
        .stream()
        .map(parent -> createClubUser(club, parentRoleEntities, parent))
        .collect(Collectors.toList());

    children.forEach(child -> createClubUser(club, getRoleEntities(Set.of(Role.CHILD)), child));

    return parentClubUsers.stream().map(ClubUserDTO::new).collect(Collectors.toList());
  }

  @Transactional
  @Override
  public ClubUserDTO registerUser(CreateUserModel userDetails) {
    Club club = clubRepository.findByClubId(userDetails.getClubId()).orElseThrow();
    Set<User> children = mapChildModelToUser(userDetails.getChildren());

    User user = constructUserEntity(userDetails);
    children.forEach(child -> createClubUser(club, getRoleEntities(Set.of(Role.CHILD)), child));
    addChildrenToParent(user, children);

    Set<Role> roles = new HashSet<>(Collections.singletonList(Role.USER));
    if (!children.isEmpty()) roles.add(Role.PARENT);

    ClubUser clubUser = createClubUser(club, getRoleEntities(roles), user);
    return new ClubUserDTO(clubUser);
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

  private ClubUser createClubUser(Club club, Set<RoleEntity> roleEntities, User user) {
    ClubUser clubUser = new ClubUser();
    user.addClubUser(clubUser);
    club.addClubUser(clubUser);
    clubUser.setRoles(roleEntities);
    return clubUserRepository.save(clubUser);
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
