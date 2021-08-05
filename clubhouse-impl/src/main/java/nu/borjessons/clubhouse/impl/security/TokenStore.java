package nu.borjessons.clubhouse.impl.security;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

public class TokenStore {
  private final ConcurrentMap<String, String> map;

  public TokenStore(ConcurrentMap<String, String> map) {
    Objects.requireNonNull(map);
    this.map = map;
  }

  public boolean isSame(String username, String token) {
    String currentToken = map.get(username);
    if (currentToken == null) return false;
    return currentToken.equals(token);
  }

  public void remove(String username) {
    map.remove(username);
  }

  public void put(String username, String token) {
    map.put(username, token);
  }
}
