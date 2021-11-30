package nu.borjessons.clubhouse.impl.controller;

import org.junit.jupiter.api.Assertions;

import nu.borjessons.clubhouse.impl.dto.UserDto;

class PrincipalControllerTest {
  private static void verifyUserDTO(UserDto expectedUserDto, UserDto userDTO) {
    Assertions.assertEquals(expectedUserDto.getUserId(), userDTO.getUserId());
    Assertions.assertEquals(expectedUserDto.getEmail(), userDTO.getEmail());
    Assertions.assertEquals(expectedUserDto.getFirstName(), userDTO.getFirstName());
    Assertions.assertEquals(expectedUserDto.getLastName(), userDTO.getLastName());
  }
}