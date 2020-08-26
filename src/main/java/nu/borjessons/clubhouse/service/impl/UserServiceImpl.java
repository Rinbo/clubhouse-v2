package nu.borjessons.clubhouse.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.UpdateUserModel;
import nu.borjessons.clubhouse.data.Address;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.ClubRole;
import nu.borjessons.clubhouse.data.ClubRole.Role;
import nu.borjessons.clubhouse.data.User;
import nu.borjessons.clubhouse.dto.UserDTO;
import nu.borjessons.clubhouse.repository.AddressRepository;
import nu.borjessons.clubhouse.repository.UserRepository;
import nu.borjessons.clubhouse.service.ClubService;
import nu.borjessons.clubhouse.service.UserService;
import nu.borjessons.clubhouse.util.ClubhouseMappers;
import nu.borjessons.clubhouse.util.ClubhouseUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserRepository userRepository;
	private final ClubService clubService;
	private final AddressRepository addressRepository;
	private final ClubhouseMappers clubhouseMappers;

	@Override
	public UserDetails loadUserByUsername(String username) {
		Optional<User> maybeUser = userRepository.findByEmail(username);
		return getOrThrowUNFE(maybeUser, username);
	}

	@Override
	public User getUserByEmail(String email) {
		Optional<User> maybeUser = userRepository.findByEmail(email);
		return getOrThrowUNFE(maybeUser, email);
	}

	@Override
	public void updateUserLoginTime(String email) {
		Optional<User> maybeUser = userRepository.findByEmail(email);
		User user = getOrThrowUNFE(maybeUser, email);
		user.setLastLoginTime(LocalDateTime.now());
		userRepository.save(user);
	}

	@Override
	public UserDTO updateUser(User user, String clubId, UpdateUserModel userDetails) {
		user.setFirstName(userDetails.getFirstName());
		user.setLastName(userDetails.getLastName());
		user.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), ClubhouseUtils.DATE_FORMAT));
		
		Set<Address> addresses = clubhouseMappers.addressModelToAddress(userDetails.getAddresses());
		
		Set<Address> oldAddresses = user.getAddresses();
		oldAddresses.stream().forEach(address -> address.setUser(null));
		user.setAddresses(new HashSet<>());
		addressRepository.deleteAll(oldAddresses);
		addresses.stream().forEach(user::addAddress);
		
		return new UserDTO(userRepository.save(user), clubId);
	}

	@Override
	public UserDTO updateUserRoles(User user, String clubId, Set<Role> roles) {
		return null;
	}

	@Override
	@Transactional
	public void removeUserFromClub(User user, Club club) {
		Set<ClubRole> clubRolesForRemoval = user.getRoles().stream().filter(clubRole -> clubRole.getClub().equals(club)).collect(Collectors.toSet());
		// Also remove user from all teams and all his children from all team (unless the children has another parent in this club (sigh)
		clubRolesForRemoval.stream().forEach(ClubRole::doOrphan);	
		userRepository.save(user);
		
	}

	@Override
	@Transactional
	public void deleteUser(User user) {
		Set<User> children = user.getChildren();
		user.setChildren(new HashSet<>());
		children.stream().forEach(child -> {
			child.removeParent(user);
			if (child.getParents().isEmpty()) {
				userRepository.delete(child);
			} else {
				userRepository.save(child);
			}
		});
		userRepository.delete(user);
	}
	
	@Override
	@Transactional
	public UserDTO updateUserChildren(User parent, Set<User> children, Club club) {
		
		Set<User> removedChildren = parent.getChildren();
		parent.setChildren(new HashSet<>());
		removedChildren.forEach(child -> child.removeParent(parent));
		
		children.forEach(child -> child.addParent(parent));
		children.addAll(removedChildren);
		List<User> savedChildren = userRepository.saveAll(children);
		
		savedChildren.forEach(child -> {
			if (child.getParents().isEmpty()) userRepository.delete(child);
		});
		
		if (!parent.getChildren().isEmpty()) {
			ClubRole clubRole = new ClubRole(Role.PARENT, parent, club);
			parent.addClubRole(clubRole);
			club.addClubRole(clubRole);
		} else {
			parent
				.getRoles()
				.stream()
				.filter(clubRole -> clubRole.getClub().equals(club) && clubRole.getRole().equals(Role.PARENT))
				.collect(Collectors.toSet())
				.forEach(ClubRole::doOrphan);
		}
		
		User savedParent = userRepository.save(parent);
		
		return new UserDTO(savedParent, club.getClubId());
	}

	@Override
	public UserDTO switchClub(User user, Club club) {
		user.setActiveClubId(club.getClubId());
		return new UserDTO(userRepository.save(user), club.getClubId());
	}

	@Override
	public UserDTO joinClub(User user, String clubId) {
		Club club = clubService.getClubByClubId(clubId);
		Set<Role> roles = new HashSet<>(Arrays.asList(Role.USER));
		if (!user.getChildren().isEmpty()) roles.add(Role.PARENT);
		clubhouseMappers.mapClubRoles(roles, user, club);
		user.setActiveClubId(clubId);
		return new UserDTO(userRepository.save(user), clubId);
	}
	
	private static User getOrThrowUNFE(Optional<User> maybeUser, String email) {
		if (maybeUser.isPresent()) return maybeUser.get();
		throw new UsernameNotFoundException(String.format("User %s could not be found", email));
	}
	
}
