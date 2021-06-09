package nu.borjessons.clubhouse.impl.controller.model.request;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class CreateUserModel implements Serializable {
  private static final long serialVersionUID = 1L;

  private Set<AddressModel> addresses = new HashSet<>();

  private Set<CreateChildRequestModel> children = new HashSet<>();

  @NotNull(message = "A user cannot be created without a club")
  private String clubId;

  @NotNull(message = "Date of birth cannot be null")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private String dateOfBirth;

  @NotNull(message = "Email cannot be null")
  @Email
  private String email;

  @NotNull(message = "First name cannot be null")
  @Size(min = 2, message = "First name must not be less than two characters")
  private String firstName;

  @NotNull(message = "Last name cannot be null")
  @Size(min = 2, message = "Last name must not be less than two characters")
  private String lastName;

  @NotNull(message = "Password cannot be null")
  @Size(min = 8, max = 16, message = "Password must be between 8 and 16 characters long")
  private String password;
}
