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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.security.TokenStore;
import nu.borjessons.clubhouse.impl.security.util.JWTUtil;
import nu.borjessons.clubhouse.impl.security.util.SecurityUtil;
import nu.borjessons.clubhouse.impl.service.ClubUserService;
import nu.borjessons.clubhouse.impl.service.UserService;

@RequiredArgsConstructor
@RestController
@Slf4j
public class TokenStoreController {
  private static ResponseEntity<String> createRedirectResponse() {
    return ResponseEntity.status(HttpStatus.TEMPORARY_REDIRECT).build();
  }

  private final ObjectMapper objectMapper;
  private final ClubUserService clubUserService;
  private final JWTUtil jwtUtil;
  private final TokenStore tokenStore;
  private final UserService userService;

  @GetMapping("/validate-token")
  public ResponseEntity<String> validateToken(HttpServletRequest request) throws JsonProcessingException {
    Optional<Cookie> optionalCookie = SecurityUtil.extractJwtCookie(request.getCookies());

    if (optionalCookie.isPresent()) {
      Cookie cookie = optionalCookie.get();
      Optional<Claims> claims = extractClaims(cookie);

      if (claims.isPresent()) {
        return validateToken(cookie, claims.get());
      } else {
        return createRedirectResponse();
      }

    } else {
      return createRedirectResponse();
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/clubs/{clubId}/revoke-token")
  public void revokeToken(@PathVariable String clubId, @RequestParam String username) {
    clubUserService.getClubUserByUsername(clubId, username).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    tokenStore.remove(username);
  }

  private ResponseEntity<String> validateToken(Cookie cookie, Claims claims) throws JsonProcessingException {
    String username = claims.getSubject();

    if (tokenStore.isSame(username, cookie.getValue())) {
      UserDto userDTO = userService.getUserByUserName(username);
      return ResponseEntity.ok(objectMapper.writeValueAsString(userDTO));
    } else {
      return createRedirectResponse();
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
