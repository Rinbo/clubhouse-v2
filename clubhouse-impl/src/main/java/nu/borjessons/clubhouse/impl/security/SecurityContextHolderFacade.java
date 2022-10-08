package nu.borjessons.clubhouse.impl.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import nu.borjessons.clubhouse.impl.data.User;

@Component
public class SecurityContextHolderFacade implements SecurityContextFacade {
  @Override
  public SecurityContext getContext() {
    return SecurityContextHolder.getContext();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getContext().getAuthentication().getAuthorities();
  }

  @Override
  public User getAuthenticationPrincipal() {
    return (User) getContext().getAuthentication().getPrincipal();
  }
}
