package nu.borjessons.clubhouse.controller.model.request;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UserLoginRequestModel {

	@NotNull(message = "Username cannot be null")
	private String username;
	@NotNull(message = "Password cannot be null")
	private String password;
}
