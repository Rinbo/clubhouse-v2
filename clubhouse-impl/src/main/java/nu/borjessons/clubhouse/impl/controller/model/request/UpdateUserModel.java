package nu.borjessons.clubhouse.impl.controller.model.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class UpdateUserModel {

  private static final long serialVersionUID = 1L;

  @NotNull(message = "First name cannot be null")
  @Size(min = 2, message = "First name must not be less than two characters")
  private String firstName;

  @NotNull(message = "Last name cannot be null")
  @Size(min = 2, message = "Last name must not be less than two characters")
  private String lastName;

  @NotNull(message = "Date of birth cannot be null")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private String dateOfBirth;

  private Set<AddressModel> addresses = new HashSet<>();
}