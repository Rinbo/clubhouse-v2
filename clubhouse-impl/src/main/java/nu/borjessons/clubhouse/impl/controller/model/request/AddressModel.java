package nu.borjessons.clubhouse.impl.controller.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Getter
@Setter
public class AddressModel implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull(message = "Street cannot be null")
  @Size(min = 10, message = "Street must not be less than ten characters")
  private String street;

  @NotNull(message = "Street cannot be null")
  @Size(min = 10, message = "Street must not be less than ten characters")
  private String postalCode;

  @NotNull(message = "City cannot be null")
  @Size(min = 2, message = "City must not be less than two characters")
  private String city;

  @NotNull(message = "Country cannot be null")
  @Size(min = 2, message = "Country must not be less than two characters")
  private String country;
}
