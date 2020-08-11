package nu.borjessons.clubhouse.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
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
import nu.borjessons.clubhouse.service.ClubhouseAbstractService;
import nu.borjessons.clubhouse.service.UserService;
import nu.borjessons.clubhouse.util.ClubhouseMappers;
import nu.borjessons.clubhouse.util.ClubhouseUtils;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ClubhouseAbstractService implements UserService {
	
	private final UserRepository userRepository;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@Transactional
	public void removeUserFromClub(User user, Club club) {
		// TODO Auto-generated method stub
		// An admin can remove a user
		// A user can leave a club. Should be the same functionality
		// Also must remove all children in this club if there are no other parents
		
		Set<ClubRole> clubRolesForRemoval = user.getRoles().stream().filter(clubRole -> clubRole.getClub().equals(club)).collect(Collectors.toSet());
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
		String clubId = club.getClubId();
		Set<User> removedChildren = parent.getChildrenInClub(clubId); 
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
		
		return new UserDTO(savedParent, clubId);
	}
}
