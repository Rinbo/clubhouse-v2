package nu.borjessons.clubhouse.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.AddressModel;
import nu.borjessons.clubhouse.controller.model.request.CreateChildRequestModel;
import nu.borjessons.clubhouse.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.data.Address;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.ClubRole;
import nu.borjessons.clubhouse.data.ClubRole.Role;
import nu.borjessons.clubhouse.data.User;

@Component
@RequiredArgsConstructor
public class ClubhouseMappers {
	
	private static final String EMAIL_EXTENTION = "@clubhouse.nu";
	private final BCryptPasswordEncoder bcryptPasswordEncoder;
	
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	public Club clubCreationModelToClub(CreateClubModel clubDetails) {
		return new Club(clubDetails.getName(), clubDetails.getType());
	}
	
	public User userCreationModelToUser(CreateUserModel userDetails) {
		User user = new User();
		user.setEmail(userDetails.getEmail().toLowerCase().trim());
		user.setFirstName(userDetails.getFirstName());
		user.setLastName(userDetails.getLastName());
		user.setEncryptedPassword(bcryptPasswordEncoder.encode(userDetails.getPassword()));
		user.setDateOfBirth(LocalDate.parse(userDetails.getDateOfBirth(), formatter));
		
		return user;
	}
	
	public List<ClubRole> rolesToClubRoles(Set<Role> roles) {
		return roles.stream().map(ClubRole::new).collect(Collectors.toList());
	}
	
	public void mapClubRoles(List<ClubRole> clubRoles, User user, Club club) {
		clubRoles.stream().forEach(clubRole -> {
			user.addClubRole(clubRole);
			club.addClubRole(clubRole);
		});
	}

	public User childCreationModelToUser(CreateChildRequestModel childModel) {
		User child = new User();
		child.setFirstName(childModel.getFirstName());
		child.setLastName(childModel.getLastName());
		child.setEmail(UUID.randomUUID().toString() + EMAIL_EXTENTION);
		child.setEncryptedPassword(bcryptPasswordEncoder.encode(UUID.randomUUID().toString()));
		child.setDateOfBirth(LocalDate.parse(childModel.getDateOfBirth(), formatter));
		return child;
	}

	public List<Address> addressModelToAddress(List<AddressModel> addressModels) {
		return addressModels.stream().map(this::addressOf).collect(Collectors.toList());
	}
	
	private Address addressOf(AddressModel addressModel) {
		Address address = new Address();
		address.setAddressId(UUID.randomUUID().toString());
		address.setStreet(addressModel.getStreet());
		address.setPostalCode(addressModel.getPostalCode());
		address.setCity(addressModel.getCity());
		address.setCountry(addressModel.getCountry());
		return address;
	}
}
