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
	private String firstName;
	private String lastName;
	private List<Long> childrenIds = new ArrayList<>();
	private List<Long> parentIds = new ArrayList<>();
	private Set<String> roles = new HashSet<>();
	private long activeClub;
	
	public UserDTO(User user) {
		email = user.getEmail();
		firstName = user.getFirstName();
		lastName = user.getLastName();
		roles = user.getActiveRoles();
		activeClub = user.getActiveClub().getId();
		childrenIds = user.getChildren().stream().map(User::getId).collect(Collectors.toList());
		parentIds = user.getParents().stream().map(User::getId).collect(Collectors.toList());
	}
}
