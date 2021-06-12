package nu.borjessons.clubhouse.impl.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Address;
import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.data.ClubRole;
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
import nu.borjessons.clubhouse.impl.service.UserService;
import nu.borjessons.clubhouse.impl.util.ClubhouseMappers;

@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {

  private final ClubRepository clubRepository;
  private final ClubService clubService;
  private final ClubhouseMappers clubhouseMappers;
  private final UserService userService;
  private final UserRepository userRepository;
  private final ClubUserRepository clubUserRepository;
  private final RoleRepository roleRepository;

  @Transactional
  @Override
  public UserDTO registerChildren(User parent, Club club, Set<CreateChildRequestModel> childModels) {
    Set<ClubRole.RoleTemp> roles = new HashSet<>();
    saveChildren(parent, childModels, roles);
    clubhouseMappers.mapClubRoles(roles, parent, club);

    return userService.updateUser(parent);
  }

  @Transactional
  @Override
  public UserDTO registerClub(CreateClubModel clubDetails) {
    Club club = clubhouseMappers.clubCreationModelToClub(clubDetails);
    Club savedClub = clubRepository.save(club);
    Set<ClubRole.RoleTemp> roles = new HashSet<>(List.of(ClubRole.RoleTemp.USER, ClubRole.RoleTemp.OWNER, ClubRole.RoleTemp.ADMIN, ClubRole.RoleTemp.LEADER));
    User user = constructUserEntity(clubDetails.getOwner(), savedClub, roles);
    return userService.createUser(user);
  }

  @Transactional
  @Override
  public UserDTO registerClub(CreateClubModel clubDetails, String clubId) {
    Club club = clubhouseMappers.clubCreationModelToClub(clubDetails, clubId);
    Club savedClub = clubRepository.save(club);
    Set<Role> myRoles = Set.of(Role.USER, Role.OWNER, Role.ADMIN, Role.LEADER);
    Set<RoleEntity> roleEntities = roleRepository.findByRoleNames(myRoles.stream().map(Role::toString).collect(Collectors.toSet()));

    Set<ClubRole.RoleTemp> roles = new HashSet<>(List.of(ClubRole.RoleTemp.USER, ClubRole.RoleTemp.OWNER, ClubRole.RoleTemp.ADMIN, ClubRole.RoleTemp.LEADER));
    User user = userRepository.save(constructUserEntity(clubDetails.getOwner(), savedClub, roles));

    final ClubUser clubUser = new ClubUser();
    clubUser.setClub(club);
    clubUser.setUser(user);
    clubUser.setRoles(roleEntities);

    return UserDTO.create(clubUserRepository.save(clubUser).getUser());
  }

  @Transactional
  @Override
  public List<UserDTO> registerFamily(FamilyRequestModel familyDetails) {
    List<CreateUserModel> parentsDetails = familyDetails.getParents();
    List<CreateChildRequestModel> childrenDetails = familyDetails.getChildren();
    Club club = clubService.getClubByClubId(familyDetails.getClubId());
    Set<ClubRole.RoleTemp> roles = new HashSet<>(List.of(ClubRole.RoleTemp.USER));
    if (!childrenDetails.isEmpty())
      roles.add(ClubRole.RoleTemp.PARENT);

    List<User> parents = parentsDetails
        .stream()
        .map(parentDetail -> constructUserEntity(parentDetail, club, roles))
        .collect(Collectors.toList());

    childrenDetails.forEach(
        childDetail -> {
          User child = clubhouseMappers.childCreationModelToUser(childDetail);
          parents.forEach(child::addParent);
        });

    return userService.createUsers(parents);
  }

  @Transactional
  @Override
  public UserDTO registerUser(CreateUserModel userDetails) {
    Club club = clubService.getClubByClubId(userDetails.getClubId());
    Set<ClubRole.RoleTemp> roles = new HashSet<>(List.of(ClubRole.RoleTemp.USER));
    User user = constructUserEntity(userDetails, club, roles);
    Set<CreateChildRequestModel> children = userDetails.getChildren();

    children.forEach(
        childModel -> {
          User child = clubhouseMappers.childCreationModelToUser(childModel);
          child.addParent(user);
          roles.add(ClubRole.RoleTemp.PARENT);
        });

    clubhouseMappers.mapClubRoles(roles, user, club);

    return userService.createUser(user);
  }

  private User constructUserEntity(CreateUserModel userDetails, Club club, Set<ClubRole.RoleTemp> roles) {
    User user = clubhouseMappers.userCreationModelToUser(userDetails);
    Set<Address> addresses = clubhouseMappers.addressModelToAddress(userDetails.getAddresses());
    addresses.forEach(user::addAddress);
    clubhouseMappers.mapClubRoles(roles, user, club);
    return user;
  }

  private void saveChildren(User parent, Set<CreateChildRequestModel> childModels, Set<ClubRole.RoleTemp> parentRoles) {
    childModels.forEach(
        childModel -> {
          User child = clubhouseMappers.childCreationModelToUser(childModel);
          child.addParent(parent);
          userService.createUser(child);
          parentRoles.add(ClubRole.RoleTemp.PARENT);
        });
  }
}
