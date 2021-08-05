package nu.borjessons.clubhouse.impl.security;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TokenStoreTest {
  private static final String USERNAME = "test@ex.com";
  private static final String TOKEN = "secret-token1234";

  @Test
  void constructorTest() {
    Assertions.assertThrows(NullPointerException.class, () -> new TokenStore(null));
  }

  @Test
  void isSame() {
    ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    map.put(USERNAME, TOKEN);
    TokenStore tokenStore = new TokenStore(map);
    Assertions.assertTrue(tokenStore.isSame(USERNAME, TOKEN));
    Assertions.assertFalse(tokenStore.isSame(USERNAME, "wrong-token"));
    Assertions.assertFalse(tokenStore.isSame("Non-existent username", TOKEN));
  }

  @Test
  void putTest() {
    ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    TokenStore tokenStore = new TokenStore(map);
    tokenStore.put(USERNAME, TOKEN);
    Assertions.assertEquals(1, map.size());
    Assertions.assertTrue(tokenStore.isSame(USERNAME, TOKEN));
  }

  @Test
  void removeTest() {
    ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    map.put(USERNAME, TOKEN);
    TokenStore tokenStore = new TokenStore(map);
    Assertions.assertEquals(1, map.size());
    tokenStore.remove(USERNAME);
    Assertions.assertEquals(0, map.size());
  }
}