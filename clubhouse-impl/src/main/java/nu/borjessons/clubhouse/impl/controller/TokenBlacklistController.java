package nu.borjessons.clubhouse.impl.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.security.TokenStore;

@RequiredArgsConstructor
@RestController
@RequestMapping("/revoke-token")
public class TokenBlacklistController {
  private final TokenStore tokenStore;

  @GetMapping
  public void revokeToken(@RequestParam String username) {
    tokenStore.remove(username);
  }
}
