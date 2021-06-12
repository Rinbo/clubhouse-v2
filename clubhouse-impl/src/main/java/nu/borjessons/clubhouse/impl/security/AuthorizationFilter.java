package nu.borjessons.clubhouse.impl.security;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import io.jsonwebtoken.Claims;
import nu.borjessons.clubhouse.impl.data.ClubUser;
import nu.borjessons.clubhouse.impl.data.RoleEntity;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.repository.ClubUserRepository;
import nu.borjessons.clubhouse.impl.service.UserService;

public class AuthorizationFilter extends BasicAuthenticationFilter {
  private static final AntPathRequestMatcher antPathRequestMatcher = new AntPathRequestMatcher("/clubs/{clubId}/**");

  private static Optional<String> getOptionalClubId(HttpServletRequest httpServletRequest) {
    final RequestMatcher.MatchResult matcher = antPathRequestMatcher.matcher(httpServletRequest);
    if (matcher.isMatch()) {
      final Map<String, String> variables = matcher.getVariables();
      return Optional.of(variables.get("clubId"));
    }
    return Optional.empty();
  }

  private final ClubUserRepository clubUserRepository;
  private final JWTUtil jwtUtil;
  private final UserService userService;

  public AuthorizationFilter(AuthenticationManager authManager, ClubUserRepository clubUserRepository, UserService userService, JWTUtil jwtUtil) {
    super(authManager);
    this.clubUserRepository = clubUserRepository;
    this.jwtUtil = jwtUtil;
    this.userService = userService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    String tokenHeader = req.getHeader(SecurityConstants.AUTHORIZATION);

    if (tokenHeader != null) {
      final String token = tokenHeader.replace(SecurityConstants.TOKEN_PREFIX, "");
      final Optional<String> optional = getOptionalClubId(req);

      UsernamePasswordAuthenticationToken authentication = optional.isPresent() ? getAuthentication(token, optional.get()) : getAuthentication(token);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    chain.doFilter(req, res);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(String token) {
    if (!jwtUtil.validateToken(token)) return null;

    Claims claims = jwtUtil.getAllClaimsFromToken(token);
    String email = claims.getSubject();

    if (email == null) return null;

    return new UsernamePasswordAuthenticationToken(userService.getUserByEmail(email), null, null);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(String token, String clubId) {
    if (!jwtUtil.validateToken(token)) return null;

    Claims claims = jwtUtil.getAllClaimsFromToken(token);
    String email = claims.getSubject();

    if (email == null) return null;

    final User user = userService.getUserByEmail(email);
    final Collection<GrantedAuthority> authorities = getClubUserAuthorities(user.getId(), clubId);
    return new UsernamePasswordAuthenticationToken(user, null, authorities);
  }

  private Collection<GrantedAuthority> getClubUserAuthorities(long userId, String clubId) {
    final Collection<RoleEntity> roleEntities = clubUserRepository.findByUserIdAndClubStringId(userId, clubId).map(ClubUser::getRoles).orElse(Set.of());
    return roleEntities.stream().map(this::getGrantedAuthority).collect(Collectors.toSet());
  }

  private GrantedAuthority getGrantedAuthority(RoleEntity roleEntity) {
    return new SimpleGrantedAuthority("ROLE_" + roleEntity.getName());
  }
}
