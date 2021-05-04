package nu.borjessons.clubhouse.impl.controller.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nu.borjessons.clubhouse.impl.data.Club.Type;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateClubModel implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull(message = "Club name cannot be null")
  @Size(min = 2, message = "Name must be longer than three characters")
  private String name;

  @NotNull(message = "Club type cannot be null")
  private Type type;

  @NotNull private @Valid CreateUserModel owner;
}