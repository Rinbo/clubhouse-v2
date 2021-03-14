package nu.borjessons.clubhouse.integration.tests.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;

@Builder
@Getter
@ToString
public class UserRest {
  private final String clubId;
  private final String userId;
  private final String firstName;
  private final String lastName;
  private final String dateOfBirth;
  private final String email;
  private final Set<String> childrenIds;
  private final Set<String> parentIds;
  private final Set<String> roles;
}
