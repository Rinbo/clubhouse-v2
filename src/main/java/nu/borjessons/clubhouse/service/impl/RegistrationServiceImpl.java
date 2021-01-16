package nu.borjessons.clubhouse.service.impl;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.CreateChildRequestModel;
import nu.borjessons.clubhouse.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.controller.model.request.FamilyRequestModel;
import nu.borjessons.clubhouse.data.Address;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.ClubRole.Role;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.UserDTO;
import nu.borjessons.clubhouse.repository.ClubRepository;
import nu.borjessons.clubhouse.service.ClubService;
import nu.borjessons.clubhouse.service.RegistrationService;
import nu.borjessons.clubhouse.service.UserService;
import nu.borjessons.clubhouse.util.ClubhouseMappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {

  private final ClubRepository clubRepository;
  private final UserService userService;
  private final ClubService clubService;
  private final ClubhouseMappers clubhouseMappers;

  @Transactional
  @Override
  public UserDTO registerClub(CreateClubModel clubDetails) {
    Club club = clubhouseMappers.clubCreationModelToClub(clubDetails);
    Club savedClub = clubRepository.save(club);
    Set<Role> roles = new HashSet<>(List.of(Role.USER, Role.OWNER, Role.ADMIN));
    User user = constructUserEntity(clubDetails.getOwner(), savedClub, roles);
    return userService.createUser(user);
  }

  @Transactional
  @Override
  public List<UserDTO> registerFamily(FamilyRequestModel familyDetails) {
    List<CreateUserModel> parentsDetails = familyDetails.getParents();
    List<CreateChildRequestModel> childrenDetails = familyDetails.getChildren();
    Club club = clubService.getClubByClubId(familyDetails.getClubId());
    Set<Role> roles = new HashSet<>(List.of(Role.USER));
    if (!childrenDetails.isEmpty()) roles.add(Role.PARENT);

    List<User> parents =
        parentsDetails.stream()
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

    Set<Role> roles = new HashSet<>(List.of(Role.USER));
    User user = constructUserEntity(userDetails, club, roles);

    Set<CreateChildRequestModel> children = userDetails.getChildren();

    children.forEach(
        childModel -> {
          User child = clubhouseMappers.childCreationModelToUser(childModel);
          child.addParent(user);
          roles.add(Role.PARENT);
        });

    clubhouseMappers.mapClubRoles(roles, user, club);

    return userService.createUser(user);
  }

  @Transactional
  @Override
  public UserDTO registerChildren(
      User parent, String clubId, Set<CreateChildRequestModel> childModels) {

    Club club = clubService.getClubByClubId(clubId);
    Set<Role> roles = new HashSet<>();
    saveChildren(parent, childModels, roles);
    clubhouseMappers.mapClubRoles(roles, parent, club);

    return userService.updateUser(parent, clubId);
  }

  private void saveChildren(
      User parent, Set<CreateChildRequestModel> childModels, Set<Role> parentRoles) {
    childModels.forEach(
        childModel -> {
          User child = clubhouseMappers.childCreationModelToUser(childModel);
          child.addParent(parent);
          userService.createUser(child);
          parentRoles.add(Role.PARENT);
        });
  }

  private User constructUserEntity(CreateUserModel userDetails, Club club, Set<Role> roles) {
    User user = clubhouseMappers.userCreationModelToUser(userDetails);
    Set<Address> addresses = clubhouseMappers.addressModelToAddress(userDetails.getAddresses());
    user.setActiveClubId(club.getClubId());
    addresses.forEach(user::addAddress);
    clubhouseMappers.mapClubRoles(roles, user, club);
    return user;
  }
}
