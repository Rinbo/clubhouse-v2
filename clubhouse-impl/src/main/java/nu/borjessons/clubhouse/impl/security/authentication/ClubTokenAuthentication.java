package nu.borjessons.clubhouse.impl.security.authentication;

import java.io.Serial;
import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

public class ClubTokenAuthentication implements Authentication {
  @Serial
  private static final long serialVersionUID = -5600312802206174372L;
  private boolean authenticated;
  private Collection<? extends GrantedAuthority> authorities;
  private final Object clubId;
  private Object principal;
  private final Object token;

  public ClubTokenAuthentication(Object token, Object clubId) {
    this.token = token;
    this.clubId = clubId;
  }

  public ClubTokenAuthentication(Object token, Object clubId, Object principal,
      Collection<? extends GrantedAuthority> authorities) {
    this.token = token;
    this.clubId = clubId;
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
    return token;
  }

  @Override
  public Object getDetails() {
    return clubId;
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
    return ((UserDetails) principal).getUsername();
  }
}
