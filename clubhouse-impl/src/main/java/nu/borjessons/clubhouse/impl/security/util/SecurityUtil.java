package nu.borjessons.clubhouse.impl.security.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {
  public static final String USER_REGISTRATION_URL = "/register/user";
  public static final String CLUB_REGISTRATION_URL = "/register/club";
  public static final String FAMILY_REGISTRATION_URL = "/register/family";
  public static final String REGISTRATION_URLS = "/register/**";
  public static final String H2_CONSOLE = "/h2-console/**";
  public static final String PUBLIC_CLUB_URLS = "/public/**";
  public static final String VALIDATE_TOKEN_URL = "/validate-token*";
  public static final String JWT_TOKEN_KEY = "jwt-token";
  public static final AntPathRequestMatcher CLUBS_URLS_MATCHER = new AntPathRequestMatcher("/clubs/{clubId}/**");
  public static final List<AntPathRequestMatcher> OPEN_ROUTES_LIST = List.of(
      new AntPathRequestMatcher(REGISTRATION_URLS),
      new AntPathRequestMatcher(H2_CONSOLE),
      new AntPathRequestMatcher(PUBLIC_CLUB_URLS),
      new AntPathRequestMatcher(VALIDATE_TOKEN_URL));

  public static Optional<Cookie> extractJwtCookie(Cookie[] cookies) {
    if (cookies == null) return Optional.empty();
    return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(JWT_TOKEN_KEY)).findFirst();
  }

  public static boolean shouldBeIgnoredByTopLevelFilter(HttpServletRequest req) {
    return OPEN_ROUTES_LIST.stream().anyMatch(pattern -> pattern.matches(req));
  }
}
