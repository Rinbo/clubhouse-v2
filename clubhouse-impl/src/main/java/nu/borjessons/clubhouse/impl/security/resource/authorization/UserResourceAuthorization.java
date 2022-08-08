package nu.borjessons.clubhouse.impl.security.resource.authorization;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.repository.UserRepository;
import nu.borjessons.clubhouse.impl.security.SecurityContextFacade;
import nu.borjessons.clubhouse.impl.util.AppUtils;

@Component
@RequiredArgsConstructor
public class UserResourceAuthorization {
  private final SecurityContextFacade securityContextFacade;
  private final UserRepository userRepository;

  public String getUserEmail(UserId userId) {
    User user = userRepository.findByUserId(userId).orElseThrow(AppUtils.createNotFoundExceptionSupplier("User not found: " + userId));
    String email = user.getEmail();

    if (user.isShowEmail()) return email;
    if (user.getUserId().equals(userId)) return email;
    if (securityContextFacade.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) return email;

    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
  }

  public void validateIsChildOfUser(UserId childId, UserId userId) {
    User user = userRepository.findByUserId(userId).orElseThrow();
    List<UserId> childrenIds = user.getChildren().stream().map(User::getUserId).toList();
    if (!childrenIds.contains(childId)) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, String.format("UserId %s is not a child of user %s", childId, userId));
    }
  }

  public void validateUserOrChild(UserId otherId, UserId principalId) {
    if (principalId.equals(otherId)) return;
    validateIsChildOfUser(otherId, principalId);
  }
}
