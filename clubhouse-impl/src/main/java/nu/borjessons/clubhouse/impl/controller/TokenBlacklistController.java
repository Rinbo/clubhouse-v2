package nu.borjessons.clubhouse.impl.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.security.TokenBlacklist;

@RequiredArgsConstructor
@RestController
@RequestMapping("/blacklist")
public class TokenBlacklistController {
  private final TokenBlacklist tokenBlacklist;

  @GetMapping
  public void blacklist(@RequestParam String username) {
    tokenBlacklist.blacklist(username);
  }
}
