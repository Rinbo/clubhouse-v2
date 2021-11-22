package nu.borjessons.clubhouse.impl.security.authentication;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import nu.borjessons.clubhouse.impl.data.User;

public class ClubTokenAuthentication implements Authentication {
  private static final long serialVersionUID = -5600312802206174372L;

  private final Object credentials;
  private final Object details;
  private Object principal;
  private Collection<? extends GrantedAuthority> authorities;
  private boolean authenticated;

  public ClubTokenAuthentication(Object credentials, Object details) {
    this.credentials = credentials;
    this.details = details;
  }

  public ClubTokenAuthentication(Object credentials, Object details, Object principal,
      Collection<? extends GrantedAuthority> authorities) {
    this.credentials = credentials;
    this.details = details;
    this.principal = principal;
    this.authorities = authorities;
    authenticated = true;
  }

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
    return details;
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