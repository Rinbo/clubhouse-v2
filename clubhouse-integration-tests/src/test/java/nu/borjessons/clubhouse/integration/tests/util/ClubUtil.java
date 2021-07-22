package nu.borjessons.clubhouse.integration.tests.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubDTO;
import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;

public class ClubUtil {
  public static CreateClubModel createClubModel(String firstName) {
    CreateClubModel clubModel = new CreateClubModel();
    clubModel.setName(firstName + " Sports");
    clubModel.setType(Club.Type.SPORT);
    clubModel.setOwner(UserUtil.createUserModel("dummy", firstName));
    return clubModel;
  }

  public static List<ClubDTO> getClubs() {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/public/clubs");
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<ClubDTO[]> responseEntity = restTemplate.getForEntity(builder.toUriString(), ClubDTO[].class);
    ClubDTO[] clubs = responseEntity.getBody();
    Assertions.assertNotNull(clubs);
    return Arrays.stream(clubs).collect(Collectors.toList());
  }

  public static ClubDTO getClub(String clubId, String token) {
    final String uri = RestUtil.getUriBuilder("/clubs/{clubId}").buildAndExpand(clubId).toUriString();
    final ResponseEntity<ClubDTO> response = RestUtil.getResponse(uri, token, ClubDTO.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static List<ClubUserDTO> getClubLeaders(String clubId, String token) throws JsonProcessingException {
    final String uri = RestUtil.getUriBuilder("/clubs/{clubId}/leaders").buildAndExpand(clubId).toUriString();
    final ResponseEntity<String> response = RestUtil.getResponse(uri, token, String.class);
    ClubUserDTO[] clubUserDTOs = RestUtil.deserializeJsonBody(response.getBody(), ClubUserDTO[].class);
    Assertions.assertNotNull(clubUserDTOs);
    return Arrays.stream(clubUserDTOs).collect(Collectors.toList());
  }

  private ClubUtil() {
    throw new IllegalStateException();
  }
}
