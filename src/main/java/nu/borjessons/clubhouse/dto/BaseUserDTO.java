package nu.borjessons.clubhouse.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nu.borjessons.clubhouse.data.User;

@Getter
@NoArgsConstructor
public class BaseUserDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userId;
	private String firstName;
	private String lastName;
	private String dateOfBirth;
	
	public BaseUserDTO(User user) {
		userId = user.getUserId();
		firstName = user.getFirstName();
		lastName = user.getLastName();
		dateOfBirth = user.getDateOfBirth().toString();
	}
}
