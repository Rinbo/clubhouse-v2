package nu.borjessons.clubhouse.impl.security.provider;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.RoleEntity;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.security.TokenStore;
import nu.borjessons.clubhouse.impl.security.authentication.ClubTokenAuthentication;
import nu.borjessons.clubhouse.impl.security.util.JWTUtil;
import nu.borjessons.clubhouse.impl.service.UserService;

@RequiredArgsConstructor
@Component
public class ClubTokenAuthenticationProvider implements AuthenticationProvider {
  private static GrantedAuthority getGrantedAuthority(RoleEntity roleEntity) {
    return new SimpleGrantedAuthority("ROLE_" + roleEntity.getName());
  }

  private final ClubUserRepository clubUserRepository;
  private final JWTUtil jwtUtil;
  private final UserService userService;
  private final TokenStore tokenStore;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String token = (String) authentication.getCredentials();
    Claims claims = jwtUtil.getAllClaimsFromToken(token);
    String username = claims.getSubject();

    if (!tokenStore.isSame(username, token)) throw new BadCredentialsException("Token is invalid");
    User user = userService.getUserByEmail(username);
    String clubId = (String) authentication.getDetails();
    Collection<GrantedAuthority> clubUserAuthorities = getClubUserAuthorities(user.getId(), clubId);
    return new ClubTokenAuthentication(token, clubId, user, clubUserAuthorities);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return ClubTokenAuthentication.class.isAssignableFrom(authentication);
  }

  private Collection<GrantedAuthority> getClubUserAuthorities(long userId, String clubId) {
    final Collection<RoleEntity> roleEntities = clubUserRepository.findByClubIdAndUserId(clubId, userId).map(ClubUser::getRoles).orElse(Set.of());
    return roleEntities.stream().map(ClubTokenAuthenticationProvider::getGrantedAuthority).collect(Collectors.toSet());
  }
}
