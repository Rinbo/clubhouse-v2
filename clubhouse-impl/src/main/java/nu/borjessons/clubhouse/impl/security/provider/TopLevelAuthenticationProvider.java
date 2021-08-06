package nu.borjessons.clubhouse.impl.security.provider;

import java.util.List;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.security.TokenStore;
import nu.borjessons.clubhouse.impl.security.authentication.TopLevelAuthentication;
import nu.borjessons.clubhouse.impl.security.util.JWTUtil;
import nu.borjessons.clubhouse.impl.service.UserService;

@RequiredArgsConstructor
@Component
public class TopLevelAuthenticationProvider implements AuthenticationProvider {
  private final JWTUtil jwtUtil;
  private final TokenStore tokenStore;
  private final UserService userService;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String token = (String) authentication.getCredentials();
    Claims claims = jwtUtil.getAllClaimsFromToken(token);
    String username = claims.getSubject();

    if (!tokenStore.isSame(username, token)) return new TopLevelAuthentication(token);

    return new TopLevelAuthentication(token, userService.getUserByEmail(username), List.of());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return TopLevelAuthentication.class.isAssignableFrom(authentication);
  }
}
