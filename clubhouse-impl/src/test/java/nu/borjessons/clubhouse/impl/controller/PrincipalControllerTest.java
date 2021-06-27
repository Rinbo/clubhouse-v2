package nu.borjessons.clubhouse.impl.controller;

import org.junit.jupiter.api.Assertions;

import nu.borjessons.clubhouse.impl.dto.UserDTO;

class PrincipalControllerTest {
  private static void verifyUserDTO(UserDTO expectedUserDTO, UserDTO userDTO) {
    Assertions.assertEquals(expectedUserDTO.getUserId(), userDTO.getUserId());
    Assertions.assertEquals(expectedUserDTO.getEmail(), userDTO.getEmail());
    Assertions.assertEquals(expectedUserDTO.getFirstName(), userDTO.getFirstName());
    Assertions.assertEquals(expectedUserDTO.getLastName(), userDTO.getLastName());
  }
}