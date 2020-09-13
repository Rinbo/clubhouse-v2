package nu.borjessons.clubhouse.controller.model.request;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class FamilyRequestModel implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@NotNull(message = "Parent filed cannot be null")
	private List<CreateUserModel> parents;
	
	@NotNull(message = "Children field cannot be null")
	private List<CreateChildRequestModel> children;
}
