package nu.borjessons.clubhouse.impl.controller;

import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.security.TokenStore;
import nu.borjessons.clubhouse.impl.security.util.JWTUtil;
import nu.borjessons.clubhouse.impl.security.util.SecurityUtil;
import nu.borjessons.clubhouse.impl.service.ClubUserService;

@RequiredArgsConstructor
@RestController
@Slf4j
public class TokenStoreController {
  private static ResponseEntity<String> createRedirectResponse(String message) {
    return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).body(message);
  }

  private final ClubUserService clubUserService;
  private final JWTUtil jwtUtil;
  private final TokenStore tokenStore;

  @GetMapping("/validate-token")
  public ResponseEntity<String> validateToken(HttpServletRequest request) {
    Optional<Cookie> optionalCookie = SecurityUtil.extractJwtCookie(request.getCookies());
    if (optionalCookie.isPresent()) {
      Cookie cookie = optionalCookie.get();
      Optional<Claims> claims = extractClaims(cookie);
      if (claims.isPresent()) {
        return validateToken(cookie, claims.get());
      } else {
        return createRedirectResponse("Token validation failed");
      }
    } else {
      return createRedirectResponse("No token available");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/clubs/{clubId}/revoke-token")
  public void revokeToken(@PathVariable String clubId, @RequestParam String username) {
    clubUserService.getClubUserByUsername(clubId, username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    tokenStore.remove(username);
  }

  private ResponseEntity<String> validateToken(Cookie cookie, Claims claims) {
    String username = claims.getSubject();
    if (tokenStore.isSame(username, cookie.getValue())) {
      return ResponseEntity.ok("Token validation succeeded");
    } else {
      return createRedirectResponse("Token did not match the one in store");
    }
  }

  private Optional<Claims> extractClaims(Cookie cookie) {
    try {
      return Optional.of(jwtUtil.getAllClaimsFromToken(cookie.getValue()));
    } catch (RuntimeException e) {
      log.debug("Failed to extract claims from token", e);
      return Optional.empty();
    }
  }
}
