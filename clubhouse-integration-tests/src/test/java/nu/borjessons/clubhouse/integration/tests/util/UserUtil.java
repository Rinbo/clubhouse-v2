package nu.borjessons.clubhouse.integration.tests.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;

import nu.borjessons.clubhouse.impl.data.key.UserId;
import nu.borjessons.clubhouse.impl.dto.BaseUserRecord;
import nu.borjessons.clubhouse.impl.dto.ClubUserDto;
import nu.borjessons.clubhouse.impl.dto.Role;
import nu.borjessons.clubhouse.impl.dto.UserDto;
import nu.borjessons.clubhouse.impl.dto.rest.AddressModel;
import nu.borjessons.clubhouse.impl.dto.rest.AdminUpdateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateChildRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.CreateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.FamilyRequestModel;
import nu.borjessons.clubhouse.impl.dto.rest.UpdateUserModel;
import nu.borjessons.clubhouse.impl.dto.rest.UserLoginRequestModel;
import nu.borjessons.clubhouse.impl.repository.UserRepository;
import nu.borjessons.clubhouse.impl.util.dev.EmbeddedDataLoader;

public class UserUtil {
  public static ClubUserDto activateChildren(String clubId, UserId userId, String token, Set<String> childrenIds) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}/activate-club-children").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Set<String>> httpEntity = RestUtil.getHttpEntity(token, childrenIds);

    ResponseEntity<ClubUserDto> response = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, ClubUserDto.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static ClubUserDto addClubUser(String clubId, UserId userId, String token, Set<String> childrenIds) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Set<String>> httpEntity = RestUtil.getHttpEntity(token, childrenIds);
    ResponseEntity<ClubUserDto> response = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, ClubUserDto.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static ClubUserDto addExistingChildToClubUser(String clubId, String token, String userId, List<String> childrenIds) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}/add-children").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<List<String>> entity = RestUtil.getHttpEntity(token, childrenIds);

    ResponseEntity<ClubUserDto> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, ClubUserDto.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static void addParentToChild(String originalParentToken, UserId childId, UserId newParentId) {
    String uri = RestUtil.getUriBuilder("/principal/add-parent")
        .queryParam("childId", childId)
        .queryParam("parentId", newParentId)
        .buildAndExpand().toUriString();

    ResponseEntity<Void> response = RestUtil.putRequest(uri, originalParentToken, Void.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  public static AdminUpdateUserModel createAdminUpdateModel(String firstName, String lastName, String dateOfBirth, Set<Role> roles) {
    AdminUpdateUserModel adminUpdateUserModel = new AdminUpdateUserModel();
    adminUpdateUserModel.setFirstName(firstName);
    adminUpdateUserModel.setLastName(lastName);
    adminUpdateUserModel.setDateOfBirth(dateOfBirth);
    adminUpdateUserModel.setShowEmail(false);
    adminUpdateUserModel.setRoles(roles);
    return adminUpdateUserModel;
  }

  public static CreateChildRequestModel createChildRequestModel(String firstName) {
    CreateChildRequestModel childRequestModel = new CreateChildRequestModel();
    childRequestModel.setFirstName(firstName);
    childRequestModel.setLastName("Childsson");
    childRequestModel.setDateOfBirth("2020-01-01");
    return childRequestModel;
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

  public static UpdateUserModel createUpdateModel(String firstName, String lastName, String dateOfBirth, boolean showEmail) {
    UpdateUserModel updateUserModel = new UpdateUserModel();
    updateUserModel.setFirstName(firstName);
    updateUserModel.setLastName(lastName);
    updateUserModel.setDateOfBirth(dateOfBirth);
    updateUserModel.setShowEmail(showEmail);
    return updateUserModel;
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

  public static CreateUserModel createUserModelWithAddress(String clubId, String firstName) {
    CreateUserModel userModel = createUserModel(clubId, firstName);
    AddressModel addressModel = new AddressModel();
    addressModel.setCity("Rome");
    addressModel.setCountry("Italy");
    addressModel.setStreet("Rome Street 10");
    addressModel.setPostalCode("12345");
    userModel.setAddresses(List.of(addressModel));
    return userModel;
  }

  public static void deleteSelf(String token) {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/principal");
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);

    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, entity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  public static List<BaseUserRecord> getChildren(String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/principal/children").buildAndExpand().toUriString();
    ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    BaseUserRecord[] baseUserRecords = RestUtil.deserializeJsonBody(response.getBody(), BaseUserRecord[].class);
    return Arrays.stream(baseUserRecords).collect(Collectors.toList());
  }

  public static ClubUserDto getClubUser(String token, String clubId, UserId userId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);

    ResponseEntity<ClubUserDto> response = restTemplate.exchange(uri, HttpMethod.GET, entity, ClubUserDto.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static ClubUserDto getClubUserPrincipal(String clubId, String token) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/principal").buildAndExpand(clubId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);
    ResponseEntity<ClubUserDto> response = restTemplate.exchange(uri, HttpMethod.GET, entity, ClubUserDto.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static List<ClubUserDto> getClubUsers(String clubId, String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users").buildAndExpand(clubId).toUriString();
    ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    ClubUserDto[] clubUserDtos = RestUtil.deserializeJsonBody(response.getBody(), ClubUserDto[].class);
    return Arrays.stream(clubUserDtos).collect(Collectors.toList());
  }

  public static List<BaseUserRecord> getClubUsersSubset(String clubId, String token, List<String> userIds) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/subset")
        .queryParam("userIds", String.join(",", userIds))
        .buildAndExpand(clubId).toUriString();
    ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    BaseUserRecord[] baseUserRecords = RestUtil.deserializeJsonBody(response.getBody(), BaseUserRecord[].class);
    return Arrays.stream(baseUserRecords).collect(Collectors.toList());
  }

  public static List<ClubUserDto> getPrincipalClubUsers(String token) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/principal/clubs/all-club-users").buildAndExpand().toUriString();

    ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());

    ClubUserDto[] clubUserDtos = RestUtil.deserializeJsonBody(response.getBody(), ClubUserDto[].class);
    return Arrays.stream(clubUserDtos).collect(Collectors.toList());
  }

  public static UserDto getSelf(String token) throws JsonProcessingException {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/principal");
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);

    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);
    UserDto userDTO = RestUtil.deserializeJsonBody(response.getBody(), UserDto.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return userDTO;
  }

  public static BaseUserRecord getUserByEmail(String email, String token) {
    String uri = RestUtil.getUriBuilder("/users").queryParam("email", email).buildAndExpand().toUriString();
    ResponseEntity<BaseUserRecord> response = RestUtil.getRequest(uri, token, BaseUserRecord.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static String getUserEmail(String token, String clubId, UserId userId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}/email").buildAndExpand(clubId, userId).toUriString();
    ResponseEntity<String> response = RestUtil.getRequest(uri, token, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static UserId getUserIdByEmail(String email, ConfigurableApplicationContext context) {
    return context.getBean(UserRepository.class).findByEmail(email).orElseThrow().getUserId();
  }

  public static List<String> getUserIdsAndSort(Collection<BaseUserRecord> baseUserRecords) {
    return baseUserRecords.stream().sorted(Comparator.comparing(BaseUserRecord::userId)).map(BaseUserRecord::userId).toList();
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

  public static ClubUserDto removeClubChildren(String clubId, UserId userId, String token, Set<String> childrenIds) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}/remove-club-children").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Set<String>> httpEntity = RestUtil.getHttpEntity(token, childrenIds);

    ResponseEntity<ClubUserDto> response = restTemplate.exchange(uri, HttpMethod.PUT, httpEntity, ClubUserDto.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return response.getBody();
  }

  public static void removeClubUser(String token, String clubId, UserId userId) {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);

    ResponseEntity<ClubUserDto> response = restTemplate.exchange(uri, HttpMethod.DELETE, entity, ClubUserDto.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
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

  public static ClubUserDto updateClubUser(AdminUpdateUserModel updateUserModel, String clubId, String token, UserId userId) throws JsonProcessingException {
    String uri = RestUtil.getUriBuilder("/clubs/{clubId}/users/{userId}").buildAndExpand(clubId, userId).toUriString();
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = RestUtil.getHttpHeaders(token);
    HttpEntity<UpdateUserModel> entity = new HttpEntity<>(updateUserModel, headers);
    ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), ClubUserDto.class);
  }

  public static UserDto updateSelf(String token, UpdateUserModel updateUserModel) throws JsonProcessingException {
    UriComponentsBuilder builder = RestUtil.getUriBuilder("/principal");
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = RestUtil.getHttpHeaders(token);
    HttpEntity<UpdateUserModel> entity = new HttpEntity<>(updateUserModel, headers);
    ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, entity, String.class);
    Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    return RestUtil.deserializeJsonBody(response.getBody(), UserDto.class);
  }

  public static ResponseEntity<String> validateToken(String token) {
    String uri = RestUtil.getUriBuilder("/validate-token").toUriString();
    RestTemplate restTemplate = RestUtil.createRestTemplate();
    HttpEntity<Void> entity = RestUtil.getVoidHttpEntity(token);
    return restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
  }

  private UserUtil() {
    throw new IllegalStateException("Utility class");
  }
}
