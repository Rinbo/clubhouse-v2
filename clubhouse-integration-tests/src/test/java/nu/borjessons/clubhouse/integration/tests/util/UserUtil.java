package nu.borjessons.clubhouse.integration.tests.util;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.dto.ClubUserDTO;
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.dto.UserDTO;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.UserLoginRequestModel;
import nu.borjessons.clubhouse.impl.util.EmbeddedDataLoader;

public class UserUtil {
  public static ClubUserDTO addExistingChildToClubUser(String clubId, String token, String userId, List<String> childrenIds) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}/add-children").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<List<String>> entity = RestUtil.getHttpEntity(token, childrenIds);

    ResponseEntity<ClubUserDTO> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, ClubUserDTO.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static CreateUserModel createUserModel(String clubId, String firstName) {
    CreateUserModel createUserModel = new CreateUserModel();
    createUserModel.setFirstName(firstName);
    createUserModel.setLastName("Genericsson");
    createUserModel.setDateOfBirth("1980-01-01");
    createUserModel.setClubId(clubId);
    createUserModel.setEmail(firstName + "@ex.com");
    createUserModel.setPassword(EmbeddedDataLoader.DEFAULT_PASSWORD);
    return createUserModel;
  }

  public static CreateChildRequestModel createChildRequestModel(String firstName) {
    CreateChildRequestModel childRequestModel = new CreateChildRequestModel();
    childRequestModel.setFirstName(firstName);
    childRequestModel.setLastName("Childsson");
    childRequestModel.setDateOfBirth("2020-01-01");
    return childRequestModel;
  }

  public static String loginUser(String email, String password) {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/login");
    RestTemplate restTemplate = new RestTemplate();

    HttpEntity<UserLoginRequestModel> httpEntity = new HttpEntity<>(new UserLoginRequestModel(email, password));
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, String.class);
    HttpHeaders headers = response.getHeaders();
    List<String> cookieHeader = headers.get("Set-Cookie");

    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assertions.assertNotNull(cookieHeader);
    return cookieHeader.get(0);
  }

  public static List<ClubUserDTO> getClubUsers(String clubId, String token) throws JsonProcessingException {
    final String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users").buildAndExpand(clubId).toUriString();
    final ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    final ClubUserDTO[] clubUserDTOs = RestUtil.deserializeJsonBody(response.getBody(), ClubUserDTO[].class);
    return Arrays.stream(clubUserDTOs).collect(Collectors.toList());
  }

  public static UserDTO getSelf(String token) throws JsonProcessingException {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/principal");
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);

    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
    UserDTO userDTO = RestUtil.deserializeJsonBody(response.getBody(), UserDTO.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return userDTO;
  }

  public static ClubUserDTO getUser(String clubId, String token, String userId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);

    ResponseEntity<ClubUserDTO> response = restTemplate.exchange(uri, HttpMethod.GET, entity, ClubUserDTO.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static FamilyRequestModel createFamilyModel(String clubId, String surname) {
    FamilyRequestModel familyRequestModel = new FamilyRequestModel();
    familyRequestModel.setClubId(clubId);

    CreateUserModel dad = createUserModel(clubId, "Pappa");
    dad.setLastName(surname);

    CreateUserModel mom = createUserModel(clubId, "Mamma");
    mom.setLastName(surname);

    CreateChildRequestModel child = createChildRequestModel("Lilleman");
    child.setLastName(surname);

    familyRequestModel.setParents(List.of(dad, mom));
    familyRequestModel.setChildren(List.of(child));
    return familyRequestModel;
  }

  public static void deleteSelf(String token) {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/principal");
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);

    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  public static ClubUserDTO getUserIdByEmail(List<ClubUserDTO> users, String email) {
    return users
        .stream()
        .filter(user -> user.getEmail().equals(email))
        .findFirst()
        .orElseThrow();
  }

  public static List<ClubUserDTO> getClubUsersByAge(String clubId, String token, int min, int max) throws JsonProcessingException {
    final String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/age-range")
        .queryParam("minAge", min)
        .queryParam("maxAge", max)
        .buildAndExpand(clubId).toUriString();

    final ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    final ClubUserDTO[] clubUserDTOs = RestUtil.deserializeJsonBody(response.getBody(), ClubUserDTO[].class);
    return Arrays.stream(clubUserDTOs).collect(Collectors.toList());
  }

  public static void removeClubUser(String clubId, String token, String userId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);

    ResponseEntity<ClubUserDTO> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, ClubUserDTO.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  public static ClubUserDTO updateClubUser(AdminUpdateUserModel updateUserModel, String clubId, String token, String userId) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = RestUtil.getHttpHeaders(token);
    HttpEntity<UpdateUserModel> entity = new HttpEntity<>(updateUserModel, headers);
    ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), ClubUserDTO.class);
  }

  public static UserDTO updateSelf(String token, UpdateUserModel updateUserModel) throws JsonProcessingException {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/principal");
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = RestUtil.getHttpHeaders(token);
    HttpEntity<UpdateUserModel> entity = new HttpEntity<>(updateUserModel, headers);
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, entity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), UserDTO.class);
  }

  public static AdminUpdateUserModel createAdminUpdateModel(String firstName, String lastName, String dateOfBirth, Set<Role> roles) {
    AdminUpdateUserModel updateUserModel = new AdminUpdateUserModel();
    updateUserModel.setFirstName(firstName);
    updateUserModel.setLastName(lastName);
    updateUserModel.setDateOfBirth(dateOfBirth);
    updateUserModel.setRoles(roles);
    return updateUserModel;
  }

  public static ClubUserDTO addClubUser(String clubId, String userId, String token, Set<String> childrenIds) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Set<String>> httpEntity = RestUtil.getHttpEntity(token, childrenIds);

    ResponseEntity<ClubUserDTO> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, ClubUserDTO.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static ClubUserDTO getClubUserPrincipal(String clubId, String token) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/principal").buildAndExpand(clubId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);
    ResponseEntity<ClubUserDTO> response = restTemplate.exchange(uri, HttpMethod.GET, entity, ClubUserDTO.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();

  }

  public static ResponseEntity<String> validateToken(String token) {
    String uri = RestUtil.getUriBuilder("/validate-token").toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);
    return restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
  }

  public static void revokeToken(String clubId, String token, String username) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/revoke-token")
        .queryParam("username", username)
        .buildAndExpand(clubId)
        .toUriString();

    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);
    ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  public static ClubUserDTO activateChildren(String clubId, String userId, String token, Set<String> childrenIds) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}/activate-club-children").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Set<String>> httpEntity = RestUtil.getHttpEntity(token, childrenIds);

    ResponseEntity<ClubUserDTO> response = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, ClubUserDTO.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static ClubUserDTO removeClubChildren(String clubId, String userId, String token, Set<String> childrenIds) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}/remove-club-children").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Set<String>> httpEntity = RestUtil.getHttpEntity(token, childrenIds);

    ResponseEntity<ClubUserDTO> response = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, ClubUserDTO.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static List<ClubUserDTO> getPrincipalClubUsers(String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/principal/clubs/all-club-users").buildAndExpand().toUriString();

    ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    ClubUserDTO[] clubUserDTOs = RestUtil.deserializeJsonBody(response.getBody(), ClubUserDTO[].class);
    return Arrays.stream(clubUserDTOs).collect(Collectors.toList());
  }

  public static BaseUserRecord getUserByEmail(String email, String token) {
    String uri = RestUtil.getUriBuilder("/users").queryParam("email", email).buildAndExpand().toUriString();
    ResponseEntity<BaseUserRecord> response = RestUtil.getRequest(uri, token, BaseUserRecord.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  private UserUtil() {
    throw new IllegalStateException("Utility class");
  }
}
