package nu.borjessons.clubhouse.impl.security.util;

import java.util.Arrays;
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
  public static final String JWT_TOKEN_KEY = "jwt-token";
  public static final AntPathRequestMatcher CLUBS_URLS_MATCHER = new AntPathRequestMatcher("/clubs/{clubId}/**");
  private static final String[] PUBLIC_URLS = new String[] {"/public/**", "/register/**", "/validate-token*", "/h2-console/**", "/images/**"};

  public static Optional<Cookie> extractJwtCookie(Cookie[] cookies) {
    if (cookies == null) return Optional.empty();
    return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(JWT_TOKEN_KEY)).findFirst();
  }

  public static boolean shouldBeIgnoredByTopLevelFilter(HttpServletRequest req) {
    return Arrays.stream(PUBLIC_URLS).map(AntPathRequestMatcher::new).anyMatch(pattern -> pattern.matches(req));
  }

  public static String[] getPublicUrls() {
    return PUBLIC_URLS;
  }
}
