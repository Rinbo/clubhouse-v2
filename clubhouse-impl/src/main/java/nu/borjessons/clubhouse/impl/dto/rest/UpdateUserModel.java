package nu.borjessons.clubhouse.impl.dto.rest;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserModel {
  private List<AddressModel> addresses = new ArrayList<>();

  @NotNull(message = "Date of birth cannot be null")
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private String dateOfBirth;

  @NotNull(message = "First name cannot be null")
  @Size(min = 2, message = "First name must not be less than two characters")
  private String firstName;

  @NotNull(message = "Last name cannot be null")
  @Size(min = 2, message = "Last name must not be less than two characters")
  private String lastName;

  private boolean showEmail;
}
