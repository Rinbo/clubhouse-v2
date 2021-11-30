package nu.borjessons.clubhouse.integration.tests.util;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.data.Club;
import nu.borjessons.clubhouse.impl.dto.ClubDto;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;

public class ClubUtil {
  public static CreateClubModel createClubModel(String firstName) {
    CreateClubModel clubModel = new CreateClubModel();
    clubModel.setName(firstName + " Sports");
    clubModel.setType(Club.Type.SPORT);
    clubModel.setOwner(UserUtil.createUserModel("dummy", firstName));
    return clubModel;
  }

  public static List<ClubDto> getClubs() {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/public/clubs");
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<ClubDto[]> responseEntity = restTemplate.getForEntity(builder.toUriString(), ClubDto[].class);
    ClubDto[] clubs = responseEntity.getBody();
    Assertions.assertNotNull(clubs);
    return Arrays.stream(clubs).collect(Collectors.toList());
  }

  public static ClubDto getClub(String clubId, String token) {
    final String uri = RestUtil.getUriBuilder("/clubs/{clubId}").buildAndExpand(clubId).toUriString();
    final ResponseEntity<ClubDto> response = RestUtil.getRequest(uri, token, ClubDto.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static List<ClubUserDto> getClubLeaders(String clubId, String token) throws JsonProcessingException {
    final String uri = RestUtil.getUriBuilder("/clubs/{clubId}/leaders").buildAndExpand(clubId).toUriString();
    final ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    ClubUserDto[] clubUserDtos = RestUtil.deserializeJsonBody(response.getBody(), ClubUserDto[].class);
    Assertions.assertNotNull(clubUserDtos);
    return Arrays.stream(clubUserDtos).collect(Collectors.toList());
  }

  public static List<ClubDto> getMyClubs(String token) {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/principal/clubs");
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);
    ResponseEntity<ClubDto[]> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, ClubDto[].class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return Arrays.stream(Objects.requireNonNull(response.getBody())).collect(Collectors.toList());
  }

  public static ClubDto getClubByPathName(String pathName) {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/public/clubs/").path(pathName);
    ResponseEntity<ClubDto> response = new RestTemplate().getForEntity(builder.toUriString(), ClubDto.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  private ClubUtil() {
    throw new IllegalStateException();
  }
}
