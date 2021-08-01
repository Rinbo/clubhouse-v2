package nu.borjessons.clubhouse.impl.security;

import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

public class TokenBlacklist {
  private final ConcurrentMap<String, Boolean> blacklist;

  public TokenBlacklist(ConcurrentMap<String, Boolean> blacklist) {
    Objects.requireNonNull(blacklist);
    this.blacklist = blacklist;
  }

  public boolean isBlacklisted(String username) {
    return blacklist.getOrDefault(username, false);
  }

  public void remove(String email) {
    blacklist.remove(email);
  }

  public void blacklist(String email) {
    blacklist.put(email, true);
  }
}
