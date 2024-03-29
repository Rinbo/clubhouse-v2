package nu.borjessons.clubhouse.impl.security.filter;

import java.io.IOException;

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
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.security.authentication.TopLevelAuthentication;
import nu.borjessons.clubhouse.impl.security.util.SecurityUtil;

@RequiredArgsConstructor
@Slf4j
public class TopLevelAuthorizationFilter extends OncePerRequestFilter {
  private final AuthenticationManager authenticationManager;

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    return SecurityUtil.CLUBS_URLS_MATCHER.matches(request) || SecurityUtil.shouldBeIgnoredByTopLevelFilter(request);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest req, @NonNull HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
    SecurityUtil.extractJwtCookie(req.getCookies()).ifPresent(cookie -> {
      TopLevelAuthentication topLevelAuthentication = new TopLevelAuthentication(cookie.getValue());
      Authentication authentication = authenticationManager.authenticate(topLevelAuthentication);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    });

    chain.doFilter(req, res);
  }
}
