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
import nu.borjessons.clubhouse.impl.dto.ClubRecord;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.rest.ClubColorRecord;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;

public class ClubUtil {
  public static CreateClubModel createClubModel(String firstName) {
    CreateClubModel clubModel = new CreateClubModel();
    clubModel.setName(firstName + " Sports");
    clubModel.setType(Club.Type.SPORT);
    clubModel.setOwner(UserUtil.createUserModel("dummy", firstName));
    return clubModel;
  }

  public static void deleteClub(String clubId, String token) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}").buildAndExpand(clubId).toUriString();
    ResponseEntity<String> response = RestUtil.deleteRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertEquals("Club deleted", response.getBody());
  }

  public static ClubRecord getClub(String clubId, String token) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}").buildAndExpand(clubId).toUriString();
    ResponseEntity<ClubRecord> response = RestUtil.getRequest(uri, token, ClubRecord.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static ClubRecord getClubByPathName(String pathName) {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/public/clubs/").path(pathName);
    ResponseEntity<ClubRecord> response = new RestTemplate().getForEntity(builder.toUriString(), ClubRecord.class);
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

  public static List<ClubRecord> getClubs() {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/public/clubs");
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<ClubRecord[]> responseEntity = restTemplate.getForEntity(builder.toUriString(), ClubRecord[].class);
    ClubRecord[] clubs = responseEntity.getBody();
    Assertions.assertNotNull(clubs);
    return Arrays.stream(clubs).collect(Collectors.toList());
  }

  public static List<ClubRecord> getMyClubs(String token) {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/principal/clubs");
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);
    ResponseEntity<ClubRecord[]> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, ClubRecord[].class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return Arrays.stream(Objects.requireNonNull(response.getBody())).collect(Collectors.toList());
  }

  public static ClubRecord updateClubColor(String clubId, String token, ClubColorRecord clubColorRecord) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/color").buildAndExpand(clubId).toUriString();
    ResponseEntity<ClubRecord> response = RestUtil.putRequest(uri, token, clubColorRecord, ClubRecord.class);

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  private ClubUtil() {
    throw new IllegalStateException();
  }
}
