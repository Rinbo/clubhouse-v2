package nu.borjessons.clubhouse.impl.security;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.RoleEntity;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.service.UserService;

@RequiredArgsConstructor
public class ClubsAuthorizationFilter extends OncePerRequestFilter {
  private static String getClubId(HttpServletRequest httpServletRequest) {
    final Map<String, String> variables = SecurityUtil.CLUBS_URLS.matcher(httpServletRequest).getVariables();
    return variables.get("clubId");
  }

  private final ClubUserRepository clubUserRepository;
  private final JWTUtil jwtUtil;
  private final UserService userService;

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return !SecurityUtil.CLUBS_URLS.matches(request);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, @NonNull HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    SecurityUtil.extractJwtCookie(req.getCookies()).ifPresent(cookie -> {
      UsernamePasswordAuthenticationToken authentication = getAuthentication(cookie.getValue(), getClubId(req));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    });

    chain.doFilter(req, res);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(String token, String clubId) {
    Claims claims = jwtUtil.getAllClaimsFromToken(token);
    String email = claims.getSubject();

    if (email == null) throw new BadCredentialsException("Token authentication failed. Could not parse claim's subject");

    final User user = userService.getUserByEmail(email);
    final Collection<GrantedAuthority> authorities = getClubUserAuthorities(user.getId(), clubId);
    return new UsernamePasswordAuthenticationToken(user, null, authorities);
  }

  private Collection<GrantedAuthority> getClubUserAuthorities(long userId, String clubId) {
    final Collection<RoleEntity> roleEntities = clubUserRepository.findByClubIdAndUserId(clubId, userId).map(ClubUser::getRoles).orElse(Set.of());
    return roleEntities.stream().map(this::getGrantedAuthority).collect(Collectors.toSet());
  }

  private GrantedAuthority getGrantedAuthority(RoleEntity roleEntity) {
    return new SimpleGrantedAuthority("ROLE_" + roleEntity.getName());
  }
}
