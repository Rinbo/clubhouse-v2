package nu.borjessons.clubhouse.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.ClubhouseMappers;
import nu.borjessons.clubhouse.controller.model.request.CreateChildRequestModel;
import nu.borjessons.clubhouse.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.ClubRole;
import nu.borjessons.clubhouse.data.ClubRole.Role;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.UserDTO;
import nu.borjessons.clubhouse.repository.ClubRepository;
import nu.borjessons.clubhouse.repository.ClubRoleRepository;
import nu.borjessons.clubhouse.repository.UserRepository;
import nu.borjessons.clubhouse.service.ClubService;
import nu.borjessons.clubhouse.service.RegistrationService;

@RequiredArgsConstructor
@Service
public class RegistrationServiceImpl implements RegistrationService {

	private final ClubRepository clubRepository;
	private final UserRepository userRepository;
	private final ClubRoleRepository clubRoleRepository;
	private final ClubService clubService;
	private final ClubhouseMappers clubhouseMappers;

	@Transactional
	@Override
	public UserDTO registerClub(CreateClubModel clubDetails) {
		Club club = clubhouseMappers.clubCreationModelToClub(clubDetails);
		User user = clubhouseMappers.userCreationModelToUser(clubDetails.getOwner());

		Club savedClub = clubRepository.save(club);
		
		user.setActiveClub(savedClub);
		
		User savedUser = userRepository.save(user);

		Set<Role> roles = new HashSet<>(Arrays.asList(Role.USER, Role.OWNER, Role.ADMIN));

		List<ClubRole> clubRoles = clubhouseMappers.rolesToClubRoles(roles);

		clubhouseMappers.mapClubRoles(clubRoles, savedUser, savedClub);

		clubRoleRepository.saveAll(clubRoles);

		return new UserDTO(savedUser);
	}

	@Transactional
	@Override
	public UserDTO registerUser(CreateUserModel userDetails) {
		User user = clubhouseMappers.userCreationModelToUser(userDetails);
		Set<CreateChildRequestModel> children = userDetails.getChildren();
		Club club = clubService.getClubById(userDetails.getClubId());
		user.setActiveClub(club);
		
		Set<Role> roles = new HashSet<>(Arrays.asList(Role.USER));
		
		children.stream().forEach(childModel -> {
			User child = clubhouseMappers.childCreationModelToUser(childModel);
			List<ClubRole> childRoles = clubhouseMappers.rolesToClubRoles(new HashSet<>(Arrays.asList(Role.CHILD)));
			roles.add(Role.PARENT);
			child.addParent(user);
			child.setActiveClub(club);
			clubhouseMappers.mapClubRoles(childRoles, child, club);
			User savedChild = userRepository.save(child);
			clubhouseMappers.mapClubRoles(childRoles, savedChild, club);
			clubRoleRepository.saveAll(childRoles);
		});
		User savedUser = userRepository.save(user);
		
		List<ClubRole> clubRoles = clubhouseMappers.rolesToClubRoles(roles);
		clubhouseMappers.mapClubRoles(clubRoles, savedUser, club);
		clubRoleRepository.saveAll(clubRoles);
		
		return new UserDTO(savedUser);
	}
}
