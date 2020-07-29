package nu.borjessons.clubhouse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.CreateChildRequestModel;
import nu.borjessons.clubhouse.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.ClubRole;
import nu.borjessons.clubhouse.data.ClubRole.Role;
import nu.borjessons.clubhouse.data.User;

@Component
@RequiredArgsConstructor
public class ClubhouseMappers {
	
	private static final String EMAIL_EXTENTION = "@clubhouse.nu";
	private final BCryptPasswordEncoder bcryptPasswordEncoder;
	
	public Club clubCreationModelToClub(CreateClubModel clubDetails) {
		return new Club(clubDetails.getName(), clubDetails.getType());
	}
	
	public User userCreationModelToUser(CreateUserModel userDetails) {
		User user = new User();
		user.setEmail(userDetails.getEmail().toLowerCase().trim());
		user.setFirstName(userDetails.getFirstName());
		user.setLastName(userDetails.getLastName());
		user.setEncryptedPassword(bcryptPasswordEncoder.encode(userDetails.getPassword()));
		
		return user;
	}
	
	public List<ClubRole> rolesToClubRoles(List<Role> roles) {
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
		return child;
	}
}
