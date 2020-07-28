package nu.borjessons.clubhouse;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.controller.model.request.CreateClubModel;
import nu.borjessons.clubhouse.controller.model.request.CreateUserModel;
import nu.borjessons.clubhouse.data.Club;
import nu.borjessons.clubhouse.data.User;

@Component
@RequiredArgsConstructor
public class ClubhouseMappers {
	
	private final BCryptPasswordEncoder bcryptPasswordEncoder;
	
	public Club clubCreationModelToClub(CreateClubModel clubDetails) {
		return new Club(clubDetails.getName(), clubDetails.getType());
	}
	
	public User userCreationModelToUser(CreateUserModel userDetails) {
		User user = new User();
		user.setEmail(userDetails.getEmail().toLowerCase().trim());
		user.setFirstname(userDetails.getFirstName());
		user.setLastName(userDetails.getLastName());
		user.setEncryptedPassword(bcryptPasswordEncoder.encode(userDetails.getPassword()));
		
		return user;
	}
	
}
