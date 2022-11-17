package nu.borjessons.clubhouse.impl.security.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtil {
  public static final List<GrantedAuthority> ADMIN_LEADER_ROLES = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_LEADER"));
  public static final AntPathRequestMatcher CLUBS_URLS_MATCHER = new AntPathRequestMatcher("/clubs/{clubId}/**");
  public static final String CLUB_REGISTRATION_URL = "/register/club";
  public static final String FAMILY_REGISTRATION_URL = "/register/family";
  public static final String JWT_TOKEN_KEY = "jwt-token";
  public static final String USER_REGISTRATION_URL = "/register/user";
  private static final List<String> PUBLIC_URLS = List.of("/public/**", "/register/**", "/validate-token*", "/images/**");

  public static Optional<Cookie> extractJwtCookie(Cookie[] cookies) {
    if (cookies == null) return Optional.empty();
    return Arrays.stream(cookies).filter(cookie -> cookie.getName().equals(JWT_TOKEN_KEY)).findFirst();
  }

  public static ResponseCookie getLogoutCookie() {
    return ResponseCookie
        .from(SecurityUtil.JWT_TOKEN_KEY, "")
        .maxAge(0)
        .secure(true)
        .sameSite("None")
        .build();
  }

  public static String[] getPublicUrls() {
    return PUBLIC_URLS.toArray(String[]::new);
  }

  // TODO remove if ok
  public static boolean shouldBeIgnoredByTopLevelFilter(HttpServletRequest req) {
    return PUBLIC_URLS.stream().map(AntPathRequestMatcher::new).anyMatch(pattern -> pattern.matches(req));
  }
}
