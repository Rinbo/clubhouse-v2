package nu.borjessons.clubhouse.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import nu.borjessons.clubhouse.data.User;

@Getter
@Setter
public class UserDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String email;
	private String userId;
	private String firstName;
	private String lastName;
	private String dateOfBirth;
	private Set<String> childrenIds = new HashSet<>();
	private Set<String> parentIds = new HashSet<>();
	private Set<String> roles = new HashSet<>();
	private String clubId;
	
	public UserDTO(User user, String clubId) {
		this.clubId = clubId;
		email = user.getEmail();
		userId = user.getUserId();
		firstName = user.getFirstName();
		lastName = user.getLastName();
		dateOfBirth = user.getDateOfBirth().toString();
		roles = user.getRolesForClub(clubId);
		childrenIds = user.getChildren().stream().map(User::getUserId).collect(Collectors.toSet());
		parentIds = user.getParents().stream().map(User::getUserId).collect(Collectors.toSet());
	}
}
