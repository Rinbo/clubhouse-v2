package nu.borjessons.clubhouse.impl.controller;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.dto.Role;

@RequiredArgsConstructor
@RestController
public class UtilityController {
  @GetMapping("/roles")
  public Collection<Role> getAllRolesNames() {
    return Arrays.stream(Role.values()).filter(role -> role != Role.SYSTEM_ADMIN).toList();
  }
}
