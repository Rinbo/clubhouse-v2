package nu.borjessons.clubhouse.impl.security;

import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.Cookie;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {
  public static final String USER_REGISTRATION_URL = "/register/user";
  public static final String CLUB_REGISTRATION_URL = "/register/club";
  public static final String FAMILY_REGISTRATION_URL = "/register/family";
  public static final String H2_CONSOLE = "/h2-console/**";
  public static final String PUBLIC_CLUB_URLS = "/public/**";
  public static final String JWT_TOKEN_KEY = "jwt-token";

  static final AntPathRequestMatcher CLUBS_URLS = new AntPathRequestMatcher("/clubs/{clubId}/**");

  public static Optional<Cookie> extractJwtCookie(Cookie[] cookies) {
    if (cookies == null) return Optional.empty();
    return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(JWT_TOKEN_KEY)).findFirst();
  }
}
