package nu.borjessons.clubhouse.dto;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;
import nu.borjessons.clubhouse.data.User;

@Data
public class UserDTO {
	
	private String email;
	private String userId;
	private String firstName;
	private String lastName;
	private Set<String> childrenIds = new HashSet<>();
	private Set<String> parentIds = new HashSet<>();
	private Set<String> roles = new HashSet<>();
	private String activeClub;
	
	public UserDTO(User user) {
		email = user.getEmail();
		userId = user.getUserId();
		firstName = user.getFirstName();
		lastName = user.getLastName();
		roles = user.getActiveRoles();
		activeClub = user.getActiveClub().getClubId();
		childrenIds = user.getChildren().stream().map(User::getUserId).collect(Collectors.toSet());
		parentIds = user.getParents().stream().map(User::getUserId).collect(Collectors.toSet());
	}
}
