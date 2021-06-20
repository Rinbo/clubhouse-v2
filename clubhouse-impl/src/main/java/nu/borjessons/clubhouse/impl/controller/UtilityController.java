package nu.borjessons.clubhouse.impl.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.Role;

@RequiredArgsConstructor
@RestController
public class UtilityController {
  @GetMapping("/roles")
  public Role[] getAllRolesNames() {
    return Role.values();
  }
}
