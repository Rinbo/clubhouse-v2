package nu.borjessons.clubhouse.impl.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.service.UserService;

@RequiredArgsConstructor
@Slf4j
public class TopLevelAuthorizationFilter extends OncePerRequestFilter {
  private final AuthenticationManager authenticationManager;
  private final JWTUtil jwtUtil;
  private final TokenStore tokenStore;
  private final UserService userService;

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return SecurityUtil.CLUBS_URLS.matches(request);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, @NonNull HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    SecurityUtil.extractJwtCookie(req.getCookies()).ifPresent(cookie -> {
      //UsernamePasswordAuthenticationToken authentication = getAuthentication(cookie.getValue());
      TopLevelAuthentication topLevelAuthentication = new TopLevelAuthentication(cookie.getValue());
      Authentication authentication = authenticationManager.authenticate(topLevelAuthentication);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    });

    chain.doFilter(req, res);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(String token) {
    Claims claims = jwtUtil.getAllClaimsFromToken(token);
    String email = claims.getSubject();

    if (email == null || !tokenStore.isSame(email, token)) {
      log.debug("Token authentication failed");
      return null;
    }

    return new UsernamePasswordAuthenticationToken(userService.getUserByEmail(email), null, null);
  }
}
