package nu.borjessons.clubhouse.impl.data;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import nu.borjessons.clubhouse.impl.data.key.UserId;

@ToString
@EqualsAndHashCode
public class AppUserDetails implements UserDetails {
  private final Collection<? extends GrantedAuthority> authorities;
  private final String hashedPassword;
  private final long id;
  private final UserId userId;
  private final String username;

  public AppUserDetails(User user) {
    hashedPassword = user.getEncryptedPassword();
    id = user.getId();
    userId = user.getUserId();
    username = user.getEmail();
    authorities = List.of();
  }

  public AppUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
    hashedPassword = user.getEncryptedPassword();
    id = user.getId();
    userId = user.getUserId();
    username = user.getEmail();
    this.authorities = authorities;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return hashedPassword;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public long getId() {
    return id;
  }

  public UserId getUserId() {
    return userId;
  }
}
