package nu.borjessons.clubhouse.impl.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import nu.borjessons.clubhouse.impl.service.UserService;

public class AuthorizationFilter extends OncePerRequestFilter {
  private final JWTUtil jwtUtil;
  private final UserService userService;

  public AuthorizationFilter(JWTUtil jwtUtil, UserService userService) {
    this.jwtUtil = jwtUtil;
    this.userService = userService;
  }

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

    if (email == null) throw new BadCredentialsException("Token authentication failed. Could not parse claim's subject");

    return new UsernamePasswordAuthenticationToken(userService.getUserByEmail(email), null, null);
  }
}
