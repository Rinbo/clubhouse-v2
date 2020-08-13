package nu.borjessons.clubhouse.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.CreateChildRequestModel;
import nu.borjessons.clubhouse.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.controller.model.request.CreateUserModel;
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

	@Transactional
	@Override
	public UserDTO registerClub(CreateClubModel clubDetails) {
		Club club = clubhouseMappers.clubCreationModelToClub(clubDetails);
		User user = clubhouseMappers.userCreationModelToUser(clubDetails.getOwner());
		Set<Address> addresses = clubhouseMappers.addressModelToAddress(clubDetails.getOwner().getAddresses());

		Club savedClub = clubRepository.save(club);
		
		user.setActiveClubId(savedClub.getClubId());
		addresses.stream().forEach(user::addAddress);
		Set<Role> roles = new HashSet<>(Arrays.asList(Role.USER, Role.OWNER, Role.ADMIN));
		clubhouseMappers.mapClubRoles(roles, user, savedClub);

		return new UserDTO(userRepository.save(user), savedClub.getClubId());
	}

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
		saveChildren(parent, childModels, club, roles);
		clubhouseMappers.mapClubRoles(roles, parent, club);
		
		return new UserDTO(userRepository.save(parent), clubId);
	}

	private void saveChildren(User parent, Set<CreateChildRequestModel> childModels, Club club, Set<Role> parentRoles) {
		childModels.stream().forEach(childModel -> {
			User child = clubhouseMappers.childCreationModelToUser(childModel);
			child.addParent(parent);
			userRepository.save(child);
			parentRoles.add(Role.PARENT);
		});
	}
}
