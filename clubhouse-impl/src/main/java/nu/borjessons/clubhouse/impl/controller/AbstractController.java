package nu.borjessons.clubhouse.impl.controller;

import nu.borjessons.clubhouse.impl.data.User;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class AbstractController {
  protected User getPrincipal() {
    return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }
}
