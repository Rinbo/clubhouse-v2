package nu.borjessons.clubhouse.impl.security;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClubsAuthorizationFilter extends OncePerRequestFilter {
  private static String getClubId(HttpServletRequest httpServletRequest) {
    final Map<String, String> variables = SecurityUtil.CLUBS_URLS.matcher(httpServletRequest).getVariables();
    return variables.get("clubId");
  }

  private final AuthenticationManager authenticationManager;

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return !SecurityUtil.CLUBS_URLS.matches(request);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, @NonNull HttpServletResponse res, FilterChain chain)
      throws ServletException, IOException {
    SecurityUtil.extractJwtCookie(req.getCookies()).ifPresent(cookie -> {
      ClubTokenAuthentication clubTokenAuthentication = new ClubTokenAuthentication(cookie.getValue(), getClubId(req));
      Authentication authentication = authenticationManager.authenticate(clubTokenAuthentication);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    });

    chain.doFilter(req, res);
  }
}
