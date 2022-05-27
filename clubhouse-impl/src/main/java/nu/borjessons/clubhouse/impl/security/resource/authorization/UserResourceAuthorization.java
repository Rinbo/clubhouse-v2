package nu.borjessons.clubhouse.impl.security.resource.authorization;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserResourceAuthorization {
  private static void throwUnauthorizedException(String message) {
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, message);
  }

  private final UserRepository userRepository;

  public void validateUserOrChild(UserId otherId, UserId principalId) {
    if (principalId.equals(otherId)) return;
    validateIsChildOfUser(otherId, principalId);
  }

  public void validateIsChildOfUser(UserId childId, UserId userId) {
    User user = userRepository.findByUserId(userId).orElseThrow();
    List<UserId> childrenIds = user.getChildren().stream().map(User::getUserId).toList();
    if (!childrenIds.contains(childId)) {
      throwUnauthorizedException(String.format("UserId %s is not a child of user %s", childId, userId));
    }
  }
}
