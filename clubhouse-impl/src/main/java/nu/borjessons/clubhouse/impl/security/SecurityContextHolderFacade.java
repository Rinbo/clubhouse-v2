package nu.borjessons.clubhouse.impl.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import nu.borjessons.clubhouse.impl.data.AppUserDetails;

@Component
public class SecurityContextHolderFacade implements SecurityContextFacade {
  @Override
  public AppUserDetails getAuthenticationPrincipal() {
    return (AppUserDetails) getContext().getAuthentication().getPrincipal();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getContext().getAuthentication().getAuthorities();
  }

  @Override
  public SecurityContext getContext() {
    return SecurityContextHolder.getContext();
  }
}
