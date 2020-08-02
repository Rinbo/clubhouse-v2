package nu.borjessons.clubhouse.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.UpdateUserModel;
import nu.borjessons.clubhouse.data.Address;
import nu.borjessons.clubhouse.data.Club;
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
		User user = getOrThrow(maybeUser, email);
		user.setLastLoginTime(LocalDateTime.now());
		userRepository.save(user);
	}

	@Override
	public UserDTO updateUser(User user, UpdateUserModel userDetails) {
		user.setFirstName(userDetails.getFirstName());
		user.setLastName(userDetails.getLastName());
		user.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), ClubhouseUtils.DATE_FORMAT));
		
		List<Address> addresses = clubhouseMappers.addressModelToAddress(userDetails.getAddresses());
		
		List<Address> oldAddresses = user.getAddresses();
		oldAddresses.stream().forEach(address -> address.setUser(null));
		user.setAddresses(new ArrayList<>());
		addressRepository.deleteAll(oldAddresses);
		addresses.stream().forEach(user::addAddress);
		
		return new UserDTO(userRepository.save(user));
	}

	@Override
	public UserDTO updateUserRoles(User user, Set<Role> roles) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeUser(User user, Club club) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteUser(User user) {
		// TODO Auto-generated method stub
		
	}

}
