package nu.borjessons.clubhouse.impl.security.authentication;

import java.io.Serial;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import nu.borjessons.clubhouse.impl.data.User;

public class TopLevelAuthentication implements Authentication {
  @Serial
  private static final long serialVersionUID = 6128259538648957141L;
  private boolean authenticated;
  private Collection<? extends GrantedAuthority> authorities;
  private final Object credentials;
  private Object principal;

  public TopLevelAuthentication(Object credentials) {
    this.credentials = credentials;
  }

  public TopLevelAuthentication(Object credentials, Object principal, Collection<? extends GrantedAuthority> authorities) {
    this.credentials = credentials;
    this.principal = principal;
    this.authorities = authorities;
    authenticated = true;
  }

  /**
   * Top level authentication contains no authorities
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public Object getCredentials() {
    return credentials;
  }

  @Override
  public Object getDetails() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return principal;
  }

  @Override
  public boolean isAuthenticated() {
    return authenticated;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    Assert.isTrue(!isAuthenticated,
        "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
    authenticated = false;
  }

  @Override
  public String getName() {
    return ((User) principal).getUsername();
  }
}
