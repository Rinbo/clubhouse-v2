package nu.borjessons.clubhouse.impl.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

import nu.borjessons.clubhouse.impl.data.User;

public interface SecurityContextFacade {
  SecurityContext getContext();

  Collection<? extends GrantedAuthority> getAuthorities();

  User getAuthenticationPrincipal();
}
