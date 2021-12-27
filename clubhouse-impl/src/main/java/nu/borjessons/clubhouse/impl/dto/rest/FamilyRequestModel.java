package nu.borjessons.clubhouse.impl.dto.rest;

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
public class FamilyRequestModel {
  @NotNull(message = "Children field cannot be null")
  private List<CreateChildRequestModel> children;

  @NotNull(message = "Club id cannot be null")
  private String clubId;

  @NotNull(message = "Parent filed cannot be null")
  private List<CreateUserModel> parents;
}
