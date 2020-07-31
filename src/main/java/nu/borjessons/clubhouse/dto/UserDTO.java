package nu.borjessons.clubhouse.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
	private List<Long> childrenIds = new ArrayList<>();
	private List<Long> parentIds = new ArrayList<>();
	private Set<String> roles = new HashSet<>();
	private String activeClub;
	
	public UserDTO(User user) {
		email = user.getEmail();
		userId = user.getUserId();
		firstName = user.getFirstName();
		lastName = user.getLastName();
		roles = user.getActiveRoles();
		activeClub = user.getActiveClub().getClubId();
		childrenIds = user.getChildren().stream().map(User::getId).collect(Collectors.toList());
		parentIds = user.getParents().stream().map(User::getId).collect(Collectors.toList());
	}
}
