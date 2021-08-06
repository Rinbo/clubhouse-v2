package nu.borjessons.clubhouse.impl.security.util;

import java.util.function.Supplier;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import nu.borjessons.clubhouse.impl.data.User;

@Component
public class ResourceAuthorization {
  private static Supplier<ResponseStatusException> getUnauthorizedException(String message) {
    return () -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
  }

  public void isChildOfUser(String childId, User user) {
    user.getChildren()
        .stream()
        .filter(child -> child.getUserId().equals(childId))
        .findFirst()
        .orElseThrow(getUnauthorizedException(String.format("UserId %s is not a child of user %s", childId, user.getUserId())));
  }
}
