package nu.borjessons.clubhouse.impl.security;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TokenBlacklistTest {
  private static final String USERNAME = "test@ex.com";

  @Test
  void constructorTest() {
    Assertions.assertThrows(NullPointerException.class, () -> new TokenBlacklist(null));
  }

  @Test
  void blacklistTest() {
    ConcurrentHashMap<String, Boolean> map = new ConcurrentHashMap<>();
    TokenBlacklist tokenBlacklist = new TokenBlacklist(map);
    tokenBlacklist.blacklist(USERNAME);
    Assertions.assertTrue(tokenBlacklist.isBlacklisted(USERNAME));
    Assertions.assertFalse(tokenBlacklist.isBlacklisted("Random"));
  }

  @Test
  void removeTest() {
    ConcurrentHashMap<String, Boolean> map = new ConcurrentHashMap<>();
    TokenBlacklist tokenBlacklist = new TokenBlacklist(map);
    tokenBlacklist.blacklist(USERNAME);
    Assertions.assertEquals(1, map.size());
    tokenBlacklist.remove(USERNAME);
    Assertions.assertEquals(0, map.size());
  }
}