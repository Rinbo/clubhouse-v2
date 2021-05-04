package nu.borjessons.clubhouse.impl.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityConstants {
  public static final String USER_REGISTRATION_URL = "/register/user";
  public static final String CLUB_REGISTRATION_URL = "/register/club";
  public static final String FAMILY_REGISTRATION_URL = "/register/family";
  public static final String H2_CONSOLE = "/h2-console/**";
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String AUTHORIZATION = "Authorization";
  public static final String PUBLIC_CLUB_URLS = "/clubs/public/**";
}
