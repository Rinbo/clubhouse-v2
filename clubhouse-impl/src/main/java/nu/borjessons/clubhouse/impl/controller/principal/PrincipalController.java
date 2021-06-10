package nu.borjessons.clubhouse.impl.controller.principal;

import javax.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import nu.borjessons.clubhouse.impl.data.ClubRole;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;
import nu.borjessons.clubhouse.impl.service.UserService;

@RequestMapping("/principal")
@RequiredArgsConstructor
@RestController
public class PrincipalController {
  private final UserService userService;

  @DeleteMapping()
  public void deleteSelf(@AuthenticationPrincipal User principal) {
    userService.deleteUser(principal);
  }

  @GetMapping("/roles")
  public ClubRole.Role[] getRolesNames() {
    return ClubRole.Role.values();
  }

  @GetMapping()
  public UserDTO getSelf(@AuthenticationPrincipal User principal) {
    return UserDTO.create(principal);
  }

  @PutMapping()
  public UserDTO updateSelf(@AuthenticationPrincipal User principal, @Valid @RequestBody UpdateUserModel userDetails) {
    return userService.updateUser(principal, userDetails);
  }
}
