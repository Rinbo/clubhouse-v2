package nu.borjessons.clubhouse.controller.model.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateChildRequestModel implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull(message = "First name cannot be null")
	@Size(min = 2, message = "First name must not be less than two characters")
	private String firstName;

	@NotNull(message = "Last name cannot be null")
	@Size(min = 2, message = "Last name must not be less than two characters")
	private String lastName;
	
}
