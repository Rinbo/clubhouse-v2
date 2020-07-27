package nu.borjessons.clubhouse.dto;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import nu.borjessons.clubhouse.data.User;

@Data
public class UserDTO {
	
	private String email;
	private String firstName;
	private String lastName;
	private Set<String> roles = new HashSet<>();
	private long activeClub;
	
	public UserDTO(User user) {
		email = user.getEmail();
		firstName = user.getFirstname();
		lastName = user.getLastName();
		roles = user.getActiveRoles();
		activeClub = user.getActiveClub().getId();
	}

}
