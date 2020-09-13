package nu.borjessons.clubhouse.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import nu.borjessons.clubhouse.repository.UserRepository;
import nu.borjessons.clubhouse.service.ClubService;
import nu.borjessons.clubhouse.service.RegistrationService;
import nu.borjessons.clubhouse.util.ClubhouseMappers;

@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {

	private final ClubRepository clubRepository;
	private final UserRepository userRepository;
	private final ClubService clubService;
	private final ClubhouseMappers clubhouseMappers;
	
	// Isn't it better to devide this into three groups (for now)
	// 1. Register club -> Public function -> calls private register club and private register user (returns user obj)
	// 2. Register user -> Public function -> calls private register user
	// 3. Register family -> Public function -> Calls private register user, private register children (takes parents (plural)

	// Generally
	// Each public function is responsible for setting the various roles and mapping details to user object (as well as children)
	// Each service in turn provides the save logic. Creating a user or club all goes through the same creat service function?
	// That way, saving a user can either have the childrens parents set
	// or have the parents children set
	
	@Transactional
	@Override
	public UserDTO registerClub(CreateClubModel clubDetails) {
		Club club = clubhouseMappers.clubCreationModelToClub(clubDetails);
		Club savedClub = clubRepository.save(club);
		Set<Role> roles = new HashSet<>(Arrays.asList(Role.USER, Role.OWNER, Role.ADMIN));
		User user = registerUser(clubDetails.getOwner(), savedClub, roles);
		return new UserDTO(user, user.getActiveClubId());
	}
	
	@Transactional
	@Override
	public void registerFamily(FamilyRequestModel familyDetails) {
		List<CreateUserModel> parentsDetails = familyDetails.getParents();
		List<CreateChildRequestModel> childrenDetails = familyDetails.getChildren();
		
		// If private registerUser function is renamed to mapping function, then parents will have to be saved at
		// then end of this function instead
		List<User> savedParents = parentsDetails.stream().map(pDetail -> {
			Club club = clubService.getClubByClubId(pDetail.getClubId());
			Set<Role> roles = new HashSet<>(Arrays.asList(Role.USER));
			if (!childrenDetails.isEmpty()) roles.add(Role.PARENT);
			return registerUser(pDetail, club, roles);
			
		}).collect(Collectors.toList());
		
		childrenDetails.stream().forEach(cDetails -> {
			User child = clubhouseMappers.childCreationModelToUser(cDetails);
			savedParents.forEach(p -> child.addParent(p));
			userRepository.save(child);
		});
	}


	// Pointless at this point but removing it would mean rewriting all tests
	@Transactional
	@Override
	public UserDTO registerUser(CreateUserModel userDetails) {
		User user = clubhouseMappers.userCreationModelToUser(userDetails);
		Set<Address> addresses = clubhouseMappers.addressModelToAddress(userDetails.getAddresses());
		addresses.stream().forEach(user::addAddress);
		
		Set<CreateChildRequestModel> children = userDetails.getChildren();
		Club club = clubService.getClubByClubId(userDetails.getClubId());
		user.setActiveClubId(club.getClubId());
		
		Set<Role> roles = new HashSet<>(Arrays.asList(Role.USER));
		
		children.stream().forEach(childModel -> {
			User child = clubhouseMappers.childCreationModelToUser(childModel);
			child.addParent(user);
			userRepository.save(child);
			roles.add(Role.PARENT);
		});
		
		clubhouseMappers.mapClubRoles(roles, user, club);
		
		return new UserDTO(userRepository.save(user), club.getClubId());
	}

	@Transactional
	@Override
	public UserDTO registerChildren(User parent, String clubId, Set<CreateChildRequestModel> childModels) {
		
		Club club = clubService.getClubByClubId(clubId);
		Set<Role> roles = new HashSet<>();
		saveChildren(parent, childModels, roles);
		clubhouseMappers.mapClubRoles(roles, parent, club);
		
		return new UserDTO(userRepository.save(parent), clubId);
	}

	private void saveChildren(User parent, Set<CreateChildRequestModel> childModels, Set<Role> parentRoles) {
		childModels.stream().forEach(childModel -> {
			User child = clubhouseMappers.childCreationModelToUser(childModel);
			child.addParent(parent);
			userRepository.save(child);
			parentRoles.add(Role.PARENT);
		});
	}
	
	// Should perhaps be renamed to only construct the user entity - not save it (like an additional mapper function)
	// Eg. constructUserEntity
	private User registerUser(CreateUserModel userDetails, Club club, Set<Role> roles) {		
		User user = clubhouseMappers.userCreationModelToUser(userDetails);		
		Set<Address> addresses = clubhouseMappers.addressModelToAddress(userDetails.getAddresses());
		user.setActiveClubId(club.getClubId());
		addresses.stream().forEach(user::addAddress);
		clubhouseMappers.mapClubRoles(roles, user, club);
		return userRepository.save(user);
	}
}
