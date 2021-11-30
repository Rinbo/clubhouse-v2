package nu.borjessons.clubhouse.integration.tests.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateClubModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.security.util.SecurityUtil;

public class RegistrationUtil {
  public static UserDto registerClubChild(String clubId, String childName, String parentId, String token) throws JsonProcessingException {
    final String uri = RestUtil
        .getUriBuilder("/clubs/{clubId}")
        .path("/register-club-children")
        .queryParam("parentId", parentId)
        .buildAndExpand(clubId).toUriString();

    CreateChildRequestModel createChildRequestModel = createChildModel(childName);
    ResponseEntity<String> response = RestUtil.postRequest(uri, token, List.of(createChildRequestModel), String.class);
    return RestUtil.deserializeJsonBody(response.getBody(), UserDto.class);
  }

  public static UserDto registerClub(CreateClubModel createClubModel) throws JsonProcessingException {
    UriComponentsBuilder builder = RestUtil.getUriBuilder(SecurityUtil.CLUB_REGISTRATION_URL);
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<CreateClubModel> httpEntity = new HttpEntity<>(createClubModel);
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), UserDto.class);
  }

  public static List<UserDto> registerFamily(FamilyRequestModel familyRequestModel) throws JsonProcessingException {
    UriComponentsBuilder builder = RestUtil.getUriBuilder(SecurityUtil.FAMILY_REGISTRATION_URL);
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<FamilyRequestModel> httpEntity = new HttpEntity<>(familyRequestModel);
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return Arrays.stream(RestUtil.deserializeJsonBody(response.getBody(), UserDto[].class)).collect(Collectors.toList());
  }

  public static UserDto registerUser(CreateUserModel createUserModel) throws JsonProcessingException {
    UriComponentsBuilder builder = RestUtil.getUriBuilder(SecurityUtil.USER_REGISTRATION_URL);
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<CreateUserModel> httpEntity = new HttpEntity<>(createUserModel);
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), UserDto.class);
  }

  public static UserDto registerChild(String childName, String parentId, String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/users/{parentId}/register-child").buildAndExpand(parentId).toUriString();
    CreateChildRequestModel createChildRequestModel = createChildModel(childName);
    ResponseEntity<String> response = RestUtil.postRequest(uri, token, createChildRequestModel, String.class);
    return RestUtil.deserializeJsonBody(response.getBody(), UserDto.class);
  }

  public static UserDto unregisterChild(String childId, String parentId, String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/users/{parentId}/unregister-child")
        .queryParam("childId", childId)
        .buildAndExpand(parentId)
        .toUriString();

    ResponseEntity<String> response = RestUtil.deleteRequest(uri, token, String.class);
    return RestUtil.deserializeJsonBody(response.getBody(), UserDto.class);
  }

  private static CreateChildRequestModel createChildModel(String childName) {
    CreateChildRequestModel createChildRequestModel = new CreateChildRequestModel();
    createChildRequestModel.setFirstName(childName);
    createChildRequestModel.setLastName(childName + "son");
    createChildRequestModel.setDateOfBirth("2020-01-01");
    return createChildRequestModel;
  }
}
