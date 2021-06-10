package nu.borjessons.clubhouse.impl.dto.rest;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class FamilyRequestModel implements Serializable {

  private static final long serialVersionUID = 1L;
  @NotNull(message = "Children field cannot be null")
  private List<CreateChildRequestModel> children;
  @NotNull(message = "Club id cannot be null")
  private String clubId;
  @NotNull(message = "Parent filed cannot be null")
  private List<CreateUserModel> parents;
}
