package nu.borjessons.clubhouse.controller.model.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NotNull(message = "First name cannot be null")
	@Size(min = 2, message = "First name must not be less than two characters")
	private String firstName;

	@NotNull(message = "Last name cannot be null")
	@Size(min = 2, message = "Last name must not be less than two characters")
	private String lastName;
	
	@NotNull(message = "Date of birth cannot be null")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private String dateOfBirth;
	
	private List<AddressModel> addresses = new ArrayList<>();

}
