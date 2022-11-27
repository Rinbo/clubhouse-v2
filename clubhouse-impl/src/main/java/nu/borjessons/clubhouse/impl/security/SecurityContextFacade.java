package nu.borjessons.clubhouse.impl.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

import nu.borjessons.clubhouse.impl.data.AppUserDetails;

public interface SecurityContextFacade {
  AppUserDetails getAuthenticationPrincipal();

  Collection<? extends GrantedAuthority> getAuthorities();

  SecurityContext getContext();
}
