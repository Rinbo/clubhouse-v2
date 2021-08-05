package nu.borjessons.clubhouse.impl.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.service.UserService;

@RequiredArgsConstructor
public class TopLevelAuthorizationFilter extends OncePerRequestFilter {
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
      UsernamePasswordAuthenticationToken authentication = getAuthentication(cookie.getValue());
      SecurityContextHolder.getContext().setAuthentication(authentication);
    });

    chain.doFilter(req, res);
  }

  private UsernamePasswordAuthenticationToken getAuthentication(String token) {
    Claims claims = jwtUtil.getAllClaimsFromToken(token);
    String email = claims.getSubject();

    if (email == null || !tokenStore.isSame(email, token)) throw new AccessDeniedException("Token authentication failed");

    return new UsernamePasswordAuthenticationToken(userService.getUserByEmail(email), null, null);
  }
}
