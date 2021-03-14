package nu.borjessons.clubhouse.impl.controller.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FamilyRequestModel implements Serializable {

  private static final long serialVersionUID = 1L;

  @NotNull(message = "Parent filed cannot be null")
  private List<CreateUserModel> parents;

  @NotNull(message = "Children field cannot be null")
  private List<CreateChildRequestModel> children;

  @NotNull(message = "Club id cannot be null")
  private String clubId;
}
