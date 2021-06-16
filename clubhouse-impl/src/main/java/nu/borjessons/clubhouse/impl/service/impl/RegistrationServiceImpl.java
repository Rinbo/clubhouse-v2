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
import nu.borjessons.clubhouse.impl.data.Role;
import nu.borjessons.clubhouse.impl.data.RoleEntity;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.repository.ClubRepository;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.repository.RoleRepository;
import nu.borjessons.clubhouse.impl.repository.UserRepository;
import nu.borjessons.clubhouse.impl.service.ClubService;
import nu.borjessons.clubhouse.impl.service.RegistrationService;
import nu.borjessons.clubhouse.impl.util.ClubhouseMappers;

@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {
  private static final Set<Role> OWNER_ROLES = Set.of(Role.USER, Role.OWNER, Role.ADMIN, Role.LEADER);

  private final ClubRepository clubRepository;
  private final ClubService clubService;
  private final ClubhouseMappers clubhouseMappers;
  private final UserRepository userRepository;
  private final ClubUserRepository clubUserRepository;
  private final RoleRepository roleRepository;

  @Transactional
  @Override
  public UserDTO registerChildren(User parent, Club club, List<CreateChildRequestModel> childDetails) {
    addChildren(parent, childDetails);
    ClubUser clubUser = clubUserRepository.findByUserIdAndClubId(parent.getId(), club.getId()).orElseThrow();
    getRoleEntities(Set.of(Role.PARENT)).forEach(clubUser::addRoleEntity);
    return UserDTO.create(clubUserRepository.save(clubUser).getUser());
  }

  @Transactional
  @Override
  public UserDTO registerClub(CreateClubModel clubDetails) {
    Club club = clubRepository.save(clubhouseMappers.clubCreationModelToClub(clubDetails));
    User user = constructUserEntity(clubDetails.getOwner());
    ClubUser clubUser = registerClubUserWithRoles(club, user, OWNER_ROLES);
    return UserDTO.create(clubUser.getUser());
  }

  @Transactional
  @Override
  public UserDTO registerClub(CreateClubModel clubDetails, String clubId) {
    Club club = clubRepository.save(clubhouseMappers.clubCreationModelToClub(clubDetails, clubId));
    User user = constructUserEntity(clubDetails.getOwner());
    ClubUser clubUser = registerClubUserWithRoles(club, user, OWNER_ROLES);
    return UserDTO.create(clubUser.getUser());
  }

  @Transactional
  @Override
  public List<UserDTO> registerFamily(FamilyRequestModel familyDetails) {
    List<CreateUserModel> parentsDetails = familyDetails.getParents();
    List<CreateChildRequestModel> childrenDetails = familyDetails.getChildren();
    Club club = clubService.getClubByClubId(familyDetails.getClubId());
    Set<Role> roles = new HashSet<>(Collections.singletonList(Role.USER));
    if (!childrenDetails.isEmpty()) roles.add(Role.PARENT);

    List<User> parents = parentsDetails
        .stream()
        .map(this::constructUserEntity)
        .map(parent -> addChildren(parent, childrenDetails))
        .collect(Collectors.toList());

    parents.forEach(parent -> registerClubUserWithRoles(club, parent, roles));

    return parents.stream().map(UserDTO::create).collect(Collectors.toList());
  }

  @Transactional
  @Override
  public UserDTO registerUser(CreateUserModel userDetails) {
    List<CreateChildRequestModel> childrenDetails = userDetails.getChildren();
    Club club = clubService.getClubByClubId(userDetails.getClubId());

    User user = constructUserEntity(userDetails);
    addChildren(user, childrenDetails);

    Set<Role> roles = new HashSet<>(Collections.singletonList(Role.USER));
    if (!childrenDetails.isEmpty()) roles.add(Role.PARENT);

    ClubUser clubUser = registerClubUserWithRoles(club, user, roles);

    return UserDTO.create(clubUser.getUser());
  }

  private User addChildren(User parent, List<CreateChildRequestModel> childrenDetails) {
    childrenDetails.forEach(
        childDetail -> {
          User child = clubhouseMappers.childCreationModelToUser(childDetail);
          child.addParent(parent);
        });
    return parent;
  }

  private ClubUser registerClubUserWithRoles(Club club, User user, Set<Role> roles) {
    Set<RoleEntity> roleEntities = getRoleEntities(roles);
    return createClubUser(club, roleEntities, user);
  }

  private ClubUser createClubUser(Club club, Set<RoleEntity> roleEntities, User user) {
    ClubUser clubUser = new ClubUser();
    clubUser.setClub(club);
    clubUser.setUser(user);
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
