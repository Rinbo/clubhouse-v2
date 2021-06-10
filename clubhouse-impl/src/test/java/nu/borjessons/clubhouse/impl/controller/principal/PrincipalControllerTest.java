package nu.borjessons.clubhouse.impl.controller.principal;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import nu.borjessons.clubhouse.impl.controller.util.TestUtil;
import nu.borjessons.clubhouse.impl.data.User;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.service.UserService;

class PrincipalControllerTest {
  private static void verifyUserDTO(UserDTO expectedUserDTO, UserDTO userDTO) {
    Assertions.assertEquals(expectedUserDTO.getUserId(), userDTO.getUserId());
    Assertions.assertEquals(expectedUserDTO.getEmail(), userDTO.getEmail());
    Assertions.assertEquals(expectedUserDTO.getFirstName(), userDTO.getFirstName());
    Assertions.assertEquals(expectedUserDTO.getLastName(), userDTO.getLastName());
  }

  @Test
  void getSelfTest() {
    final User user = TestUtil.getClubUser(TestUtil.USER_1);
    final UserService userService = Mockito.mock(UserService.class);
    final PrincipalController principalController = new PrincipalController(userService);

    UserDTO userDTO = principalController.getSelf(user);
    UserDTO expectedUserDTO = UserDTO.create(user);
    verifyUserDTO(expectedUserDTO, userDTO);
    Mockito.verifyNoInteractions(userService);
  }
}