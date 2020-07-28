package nu.borjessons.clubhouse.controller.model.request;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserLoginRequestModel {

	@NotNull(message = "Username cannot be null")
	private String username;
	@NotNull(message = "Password cannot be null")
	private String password;
}
